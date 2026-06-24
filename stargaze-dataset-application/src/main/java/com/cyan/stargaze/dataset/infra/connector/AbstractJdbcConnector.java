package com.cyan.stargaze.dataset.infra.connector;

import com.cyan.arch.common.api.SilentException;
import com.cyan.stargaze.dataset.domain.datasource.valobj.ColumnValObj;
import com.cyan.stargaze.dataset.domain.datasource.valobj.DataSourceConfig;
import com.cyan.stargaze.dataset.domain.datasource.valobj.TableMetaValObj;
import com.cyan.stargaze.dataset.domain.datasource.valobj.TableSampleValObj;
import com.cyan.stargaze.dataset.domain.datasource.valobj.TableSchemaValObj;
import com.cyan.stargaze.dataset.infra.config.DatasetSqlProperties;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 基于 JDBC 的数据源适配器抽象基类。
 * <p>
 * 子类只需提供 {@link #driverClassName()} 与 {@link #buildUrl(DataSourceConfig)}、
 * 以及库/表/列查询 SQL 模板(默认使用 information_schema,兼容大部分关系/OLAP 库)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
public abstract class AbstractJdbcConnector implements DataSourceConnector {

    /** SQL 执行配置(超时/行数上限) */
    @Autowired
    protected DatasetSqlProperties sqlProperties;

    /**
     * JDBC 驱动类名
     */
    protected abstract String driverClassName();

    /**
     * 由连接配置构造 JDBC URL
     */
    protected abstract String buildUrl(DataSourceConfig config);

    /**
     * 列库 SQL(返回单列 schema 名)
     */
    protected String listSchemasSql() {
        return "SELECT schema_name FROM information_schema.schemata ORDER BY schema_name";
    }

    /**
     * 列表 SQL(返回单列表名)
     */
    protected String listTablesSql() {
        return "SELECT table_name FROM information_schema.tables WHERE table_schema = ? ORDER BY table_name";
    }

    /**
     * 描述表 SQL(返回 column_name, data_type, is_nullable, ordinal_position;可选列 column_comment, is_primary_key)
     */
    protected String describeTableSql() {
        return "SELECT column_name, data_type, is_nullable, ordinal_position "
                + "FROM information_schema.columns WHERE table_schema = ? AND table_name = ? "
                + "ORDER BY ordinal_position";
    }

    /**
     * 富列表表 SQL(返回 table_name, table_comment, field_count, row_count;返回 null 表示走默认逐表降级)。
     * <p>
     * 参数 1=schema(可选 keyword 模糊)。方言覆盖以填充注释/行数/字段数。
     */
    protected String listTablesRichSql() {
        return null;
    }

    /**
     * 统计表行数 SQL(返回单数值)。默认 select count(*),方言可覆盖为估算(reltuples/TABLE_ROWS)。
     */
    protected String countTableSql(String schema, String tableName) {
        return "SELECT count(*) FROM " + quoteIdentifier(schema) + "." + quoteIdentifier(tableName);
    }

    /**
     * 采样 SQL
     */
    protected String sampleSql(String schema, String tableName, int limit) {
        return "SELECT * FROM " + quoteIdentifier(schema) + "." + quoteIdentifier(tableName) + " LIMIT " + limit;
    }

    /**
     * 标识符引号(默认双引号,MySQL 系列覆盖为反引号)
     */
    protected char identifierQuote() {
        return '"';
    }

    @Override
    public String quoteIdentifier(String name) {
        char q = identifierQuote();
        return q + name.replace(String.valueOf(q), q + "" + q) + q;
    }

    protected Connection getConnection(DataSourceConfig config) {
        try {
            Class.forName(driverClassName());
            String url = config.getJdbcUrl();
            if (url == null || url.isBlank()) {
                url = buildUrl(config);
            }
            return DriverManager.getConnection(url, config.getUsername(), config.getPassword());
        } catch (Exception e) {
            throw new SilentException("数据源连接失败: " + e.getMessage());
        }
    }

    @Override
    public void testConnection(DataSourceConfig config) {
        try (Connection conn = getConnection(config);
             PreparedStatement ps = conn.prepareStatement("SELECT 1")) {
            ps.executeQuery();
        } catch (Exception e) {
            throw new SilentException("数据源连接测试失败: " + e.getMessage());
        }
    }

    @Override
    public List<String> listSchemas(DataSourceConfig config) {
        List<String> schemas = new ArrayList<>();
        try (Connection conn = getConnection(config);
             PreparedStatement ps = conn.prepareStatement(listSchemasSql());
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String name = rs.getString(1);
                if (!isSystemSchema(name)) {
                    schemas.add(name);
                }
            }
        } catch (Exception e) {
            throw new SilentException("查询库列表失败: " + e.getMessage());
        }
        return schemas;
    }

    @Override
    public List<String> listTables(DataSourceConfig config, String schema) {
        List<String> tables = new ArrayList<>();
        try (Connection conn = getConnection(config);
             PreparedStatement ps = conn.prepareStatement(listTablesSql())) {
            ps.setString(1, schema);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tables.add(rs.getString(1));
                }
            }
        } catch (Exception e) {
            throw new SilentException("查询表列表失败: " + e.getMessage());
        }
        return tables;
    }

    @Override
    public TableSchemaValObj describeTable(DataSourceConfig config, String schema, String tableName) {
        List<ColumnValObj> columns = new ArrayList<>();
        try (Connection conn = getConnection(config);
             PreparedStatement ps = conn.prepareStatement(describeTableSql())) {
            ps.setString(1, schema);
            ps.setString(2, tableName);
            try (ResultSet rs = ps.executeQuery()) {
                java.sql.ResultSetMetaData meta = rs.getMetaData();
                int commentIdx = findColumn(meta, "column_comment");
                int pkIdx = findColumn(meta, "is_primary_key");
                while (rs.next()) {
                    columns.add(new ColumnValObj()
                            .setName(rs.getString("column_name"))
                            .setDataType(rs.getString("data_type"))
                            .setNullable("YES".equalsIgnoreCase(rs.getString("is_nullable")))
                            .setComment(commentIdx > 0 ? rs.getString(commentIdx) : null)
                            .setPrimaryKey(pkIdx > 0 && rs.getBoolean(pkIdx)));
                }
            }
        } catch (Exception e) {
            throw new SilentException("查询表结构失败: " + e.getMessage());
        }
        return new TableSchemaValObj()
                .setTableName(tableName)
                .setColumns(columns);
    }

    @Override
    public List<TableMetaValObj> listTablesRich(DataSourceConfig config, String schema, String keyword) {
        String richSql = listTablesRichSql();
        if (richSql != null) {
            return listTablesRichBySql(config, schema, keyword, richSql);
        }
        // 默认降级:listTables + 逐表 describeTable 取 fieldCount,注释/行数为空
        List<TableMetaValObj> result = new ArrayList<>();
        for (String table : listTables(config, schema)) {
            if (keyword != null && !keyword.isBlank() && !table.toLowerCase().contains(keyword.toLowerCase())) {
                continue;
            }
            int fieldCount = describeTable(config, schema, table).getColumns().size();
            result.add(new TableMetaValObj()
                    .setSchema(schema)
                    .setTableName(table)
                    .setFieldCount(fieldCount));
        }
        return result;
    }

    @Override
    public long countTable(DataSourceConfig config, String schema, String tableName) {
        try (Connection conn = getConnection(config);
             PreparedStatement ps = conn.prepareStatement(countTableSql(schema, tableName))) {
            ps.setQueryTimeout(sqlProperties.getQueryTimeoutSeconds());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0L;
            }
        } catch (Exception e) {
            // best-effort,失败返回 -1
            return -1L;
        }
    }

    /**
     * 按富 SQL 查询表元信息(table_name, table_comment, field_count, row_count)
     */
    private List<TableMetaValObj> listTablesRichBySql(DataSourceConfig config, String schema, String keyword, String sql) {
        List<TableMetaValObj> result = new ArrayList<>();
        try (Connection conn = getConnection(config);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, schema);
            try (ResultSet rs = ps.executeQuery()) {
                java.sql.ResultSetMetaData meta = rs.getMetaData();
                int commentIdx = findColumn(meta, "table_comment");
                int fieldCountIdx = findColumn(meta, "field_count");
                int rowCountIdx = findColumn(meta, "row_count");
                while (rs.next()) {
                    String name = rs.getString("table_name");
                    if (keyword != null && !keyword.isBlank() && !name.toLowerCase().contains(keyword.toLowerCase())) {
                        continue;
                    }
                    result.add(new TableMetaValObj()
                            .setSchema(schema)
                            .setTableName(name)
                            .setTableComment(commentIdx > 0 ? rs.getString(commentIdx) : null)
                            .setFieldCount(fieldCountIdx > 0 ? rs.getInt(fieldCountIdx) : null)
                            .setRowCount(rowCountIdx > 0 ? rs.getLong(rowCountIdx) : null));
                }
            }
        } catch (Exception e) {
            throw new SilentException("查询表列表失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 按列名查找 ResultSet 列索引(1 起始,不存在返回 -1)。
     * <p>
     * ResultSetMetaData 无 findColumn,遍历 getColumnLabel 匹配(大小写不敏感)。
     */
    protected int findColumn(java.sql.ResultSetMetaData meta, String columnName) {
        try {
            int count = meta.getColumnCount();
            for (int i = 1; i <= count; i++) {
                if (columnName.equalsIgnoreCase(meta.getColumnLabel(i))) {
                    return i;
                }
            }
            return -1;
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public TableSampleValObj sampleTable(DataSourceConfig config, String schema, String tableName, int limit) {
        try (Connection conn = getConnection(config);
             PreparedStatement ps = conn.prepareStatement(sampleSql(schema, tableName, limit));
             ResultSet rs = ps.executeQuery()) {
            return readSample(rs);
        } catch (Exception e) {
            throw new SilentException("采样表数据失败: " + e.getMessage());
        }
    }

    @Override
    public TableSchemaValObj describeSql(DataSourceConfig config, String schema, String sql) {
        String wrapped = "select * from (" + sql + ") __t__ where 1 = 0";
        try (Connection conn = getConnection(config)) {
            applySchema(conn, schema);
            try (PreparedStatement ps = conn.prepareStatement(wrapped)) {
                ps.setQueryTimeout(sqlProperties.getQueryTimeoutSeconds());
                try (ResultSet rs = ps.executeQuery()) {
                    List<ColumnValObj> columns = readColumns(rs.getMetaData());
                    return new TableSchemaValObj().setTableName("__sql__").setColumns(columns);
                }
            }
        } catch (Exception e) {
            throw new SilentException("解析 SQL schema 失败: " + e.getMessage());
        }
    }

    @Override
    public TableSampleValObj sampleSql(DataSourceConfig config, String schema, String sql, int limit) {
        int capped = Math.min(limit, sqlProperties.getMaxSampleRows());
        String wrapped = "select * from (" + sql + ") __t__ limit " + capped;
        try (Connection conn = getConnection(config)) {
            applySchema(conn, schema);
            try (PreparedStatement ps = conn.prepareStatement(wrapped)) {
                ps.setQueryTimeout(sqlProperties.getQueryTimeoutSeconds());
                try (ResultSet rs = ps.executeQuery()) {
                    return readSample(rs);
                }
            }
        } catch (Exception e) {
            throw new SilentException("采样 SQL 数据失败: " + e.getMessage());
        }
    }

    /**
     * 设置连接 schema(兼容 PG search_path / 多 schema,可空跳过)
     */
    protected void applySchema(Connection conn, String schema) {
        if (schema == null || schema.isBlank()) {
            return;
        }
        try {
            conn.setSchema(schema);
        } catch (Exception ignored) {
            // 部分驱动不支持 setSchema,忽略交由 SQL 自身限定
        }
    }

    /**
     * 读取 ResultSet 字段元信息
     */
    protected List<ColumnValObj> readColumns(ResultSetMetaData meta) throws Exception {
        List<ColumnValObj> columns = new ArrayList<>();
        int count = meta.getColumnCount();
        for (int i = 1; i <= count; i++) {
            columns.add(new ColumnValObj()
                    .setName(meta.getColumnLabel(i))
                    .setDataType(meta.getColumnTypeName(i))
                    .setNullable(meta.isNullable(i) != ResultSetMetaData.columnNoNulls)
                    .setPrimaryKey(false));
        }
        return columns;
    }

    /**
     * 读取 ResultSet 为采样结果(列名 + 行)
     */
    protected TableSampleValObj readSample(ResultSet rs) throws Exception {
        ResultSetMetaData meta = rs.getMetaData();
        int count = meta.getColumnCount();
        List<String> columns = new ArrayList<>(count);
        for (int i = 1; i <= count; i++) {
            columns.add(meta.getColumnLabel(i));
        }
        List<Map<String, Object>> rows = new ArrayList<>();
        while (rs.next()) {
            Map<String, Object> row = new LinkedHashMap<>();
            for (int i = 1; i <= count; i++) {
                row.put(columns.get(i - 1), rs.getObject(i));
            }
            rows.add(row);
        }
        return new TableSampleValObj().setColumns(columns).setRows(rows);
    }

    /**
     * 是否系统库(过滤 information_schema/pg_catalog/mysql 等)
     */
    protected boolean isSystemSchema(String name) {
        if (name == null) {
            return true;
        }
        return name.equalsIgnoreCase("information_schema")
                || name.equalsIgnoreCase("pg_catalog")
                || name.equalsIgnoreCase("pg_toast")
                || name.equalsIgnoreCase("mysql")
                || name.equalsIgnoreCase("sys")
                || name.equalsIgnoreCase("performance_schema")
                || name.startsWith("INFORMATION_SCHEMA");
    }

    /**
     * 简单类型判断辅助
     */
    protected boolean isNumericType(int sqlType) {
        return sqlType == Types.INTEGER || sqlType == Types.BIGINT || sqlType == Types.DECIMAL
                || sqlType == Types.NUMERIC || sqlType == Types.DOUBLE || sqlType == Types.FLOAT
                || sqlType == Types.REAL || sqlType == Types.SMALLINT || sqlType == Types.TINYINT;
    }
}
