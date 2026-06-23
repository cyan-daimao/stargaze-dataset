package com.cyan.stargaze.dataset.infra.connector;

import com.cyan.arch.common.api.SilentException;
import com.cyan.stargaze.dataset.domain.datasource.valobj.ColumnValObj;
import com.cyan.stargaze.dataset.domain.datasource.valobj.DataSourceConfig;
import com.cyan.stargaze.dataset.domain.datasource.valobj.TableSampleValObj;
import com.cyan.stargaze.dataset.domain.datasource.valobj.TableSchemaValObj;

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
     * 描述表 SQL(返回 column_name, data_type, is_nullable, ordinal_position)
     */
    protected String describeTableSql() {
        return "SELECT column_name, data_type, is_nullable, ordinal_position "
                + "FROM information_schema.columns WHERE table_schema = ? AND table_name = ? "
                + "ORDER BY ordinal_position";
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

    protected String quoteIdentifier(String name) {
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
                while (rs.next()) {
                    columns.add(new ColumnValObj()
                            .setName(rs.getString("column_name"))
                            .setDataType(rs.getString("data_type"))
                            .setNullable("YES".equalsIgnoreCase(rs.getString("is_nullable")))
                            .setPrimaryKey(false));
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
    public TableSampleValObj sampleTable(DataSourceConfig config, String schema, String tableName, int limit) {
        List<String> columns = new ArrayList<>();
        List<Map<String, Object>> rows = new ArrayList<>();
        try (Connection conn = getConnection(config);
             PreparedStatement ps = conn.prepareStatement(sampleSql(schema, tableName, limit));
             ResultSet rs = ps.executeQuery()) {
            ResultSetMetaData meta = rs.getMetaData();
            int count = meta.getColumnCount();
            for (int i = 1; i <= count; i++) {
                columns.add(meta.getColumnLabel(i));
            }
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= count; i++) {
                    row.put(columns.get(i - 1), rs.getObject(i));
                }
                rows.add(row);
            }
        } catch (Exception e) {
            throw new SilentException("采样表数据失败: " + e.getMessage());
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
