package com.cyan.stargaze.dataset.infra.connector.join;

import com.cyan.arch.common.api.Assert;
import com.cyan.arch.common.api.SilentException;
import com.cyan.stargaze.dataset.domain.datasource.valobj.ColumnValObj;
import com.cyan.stargaze.dataset.domain.datasource.valobj.DataSourceConfig;
import com.cyan.stargaze.dataset.domain.datasource.valobj.TableSchemaValObj;
import com.cyan.stargaze.dataset.domain.dataset.config.JoinClause;
import com.cyan.stargaze.dataset.domain.dataset.config.JoinConfig;
import com.cyan.stargaze.dataset.domain.dataset.config.JoinCondition;
import com.cyan.stargaze.dataset.domain.dataset.config.TableRef;
import com.cyan.stargaze.dataset.enums.DatasourceType;
import com.cyan.stargaze.dataset.enums.JoinType;
import com.cyan.stargaze.dataset.infra.connector.DataSourceConnector;
import com.cyan.stargaze.dataset.infra.connector.DataSourceConnectorFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * JOIN 数据集 SQL 编译器。
 * <p>
 * 将 {@link JoinConfig}(主表 + N 个 join,每个 join 多个 AND 条件)编译为可执行 SQL,
 * 并对表名/字段名做白名单校验,杜绝未校验输入拼接。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class JoinSqlCompiler {

    private final DataSourceConnectorFactory connectorFactory;

    /**
     * 编译 JOIN config 为可执行 SQL + 字段列表。
     *
     * @param config JOIN 配置(已通过 validate)
     * @param dsConfig 数据源连接配置
     * @param type   数据源类型
     */
    public CompiledJoin compile(JoinConfig config, DataSourceConfig dsConfig, DatasourceType type) {
        DataSourceConnector connector = connectorFactory.get(type);

        // 主表:按自身 schema 校验白名单 + describeTable
        TableRef mainRef = config.getMainTable();
        String mainSchema = mainRef.getSchema();
        assertTable(connector, dsConfig, mainRef.getTableName(), mainSchema);
        String mainTable = mainRef.getTableName();
        String mainAlias = mainTable;
        Map<String, ColumnValObj> mainCols = describeAsMap(connector, dsConfig, mainSchema, mainTable);

        // alias -> 字段白名单; alias -> schema(用于限定表名)
        Map<String, Map<String, ColumnValObj>> tableCols = new LinkedHashMap<>();
        Map<String, String> aliasSchema = new LinkedHashMap<>();
        tableCols.put(mainAlias, mainCols);
        aliasSchema.put(mainAlias, mainSchema);

        // 编译 join 子句 + 累积 from 片段
        List<String> fromParts = new ArrayList<>();
        fromParts.add(qualified(mainSchema, mainTable, connector) + " " + connector.quoteIdentifier(mainAlias));

        for (JoinClause clause : config.getJoins()) {
            JoinType joinType = clause.getJoinType();
            Assert.notNull(joinType, new SilentException("JOIN 类型不能为空"));
            TableRef joinRef = clause.getTable();
            String joinSchema = joinRef.getSchema();
            assertTable(connector, dsConfig, joinRef.getTableName(), joinSchema);
            String joinTable = joinRef.getTableName();
            String joinAlias = joinTable;
            Assert.isTrue(!tableCols.containsKey(joinAlias),
                    new SilentException("JOIN 关联表重复: " + joinTable));
            // 左字段须在当前 join 表加入之前已出现的表中(避免自连接)
            Map<String, Map<String, ColumnValObj>> priorCols = new LinkedHashMap<>(tableCols);
            Map<String, ColumnValObj> joinCols = describeAsMap(connector, dsConfig, joinSchema, joinTable);
            tableCols.put(joinAlias, joinCols);
            aliasSchema.put(joinAlias, joinSchema);

            // ON 条件(多 AND)
            Assert.notEmpty(clause.getConditions(), new SilentException("JOIN 缺少关联条件: " + joinTable));
            List<String> onParts = new ArrayList<>();
            for (JoinCondition cond : clause.getConditions()) {
                Assert.isTrue(cond.getOperator() == null || "=".equals(cond.getOperator().trim()),
                        new SilentException("一期 JOIN 仅支持 = 运算符"));
                String leftAlias = resolveFieldOwner(cond.getLeftField(), priorCols);
                Assert.notNull(leftAlias, new SilentException("JOIN 左字段不存在: " + cond.getLeftField()));
                Assert.isTrue(joinCols.containsKey(cond.getRightField().toLowerCase(Locale.ROOT)),
                        new SilentException("JOIN 右字段不存在: " + cond.getRightField()));
                String leftCol = tableCols.get(leftAlias).get(cond.getLeftField().toLowerCase(Locale.ROOT)).getName();
                String rightCol = joinCols.get(cond.getRightField().toLowerCase(Locale.ROOT)).getName();
                onParts.add(connector.quoteIdentifier(leftAlias) + "." + connector.quoteIdentifier(leftCol)
                        + " = " + connector.quoteIdentifier(joinAlias) + "." + connector.quoteIdentifier(rightCol));
            }
            fromParts.add(joinType.sql() + " " + qualified(joinSchema, joinTable, connector) + " " + connector.quoteIdentifier(joinAlias)
                    + " on " + String.join(" and ", onParts));
        }

        // select 字段:每张表的列,重名用 表别名.列名 作 originName
        List<ColumnValObj> columns = new ArrayList<>();
        List<String> selectParts = new ArrayList<>();
        for (Map.Entry<String, Map<String, ColumnValObj>> entry : tableCols.entrySet()) {
            String alias = entry.getKey();
            Map<String, ColumnValObj> ownCols = entry.getValue();
            for (ColumnValObj col : ownCols.values()) {
                String colName = col.getName();
                boolean dup = isDuplicate(colName, tableCols);
                String origin = dup ? alias + "." + colName : colName;
                selectParts.add(connector.quoteIdentifier(alias) + "." + connector.quoteIdentifier(colName)
                        + " as " + connector.quoteIdentifier(origin));
                columns.add(new ColumnValObj()
                        .setName(origin)
                        .setDataType(col.getDataType())
                        .setNullable(col.getNullable())
                        .setPrimaryKey(false));
            }
        }

        String sql = "select " + String.join(", ", selectParts)
                + " from " + String.join(" ", fromParts);
        return new CompiledJoin().setSql(sql).setColumns(columns);
    }

    /**
     * 字段是否在多张表重复出现
     */
    private boolean isDuplicate(String colName, Map<String, Map<String, ColumnValObj>> tableCols) {
        int count = 0;
        for (Map<String, ColumnValObj> cols : tableCols.values()) {
            if (cols.containsKey(colName.toLowerCase(Locale.ROOT))) {
                count++;
            }
        }
        return count > 1;
    }

    /**
     * 在给定表中查找字段所属表别名(返回第一个命中)
     */
    private String resolveFieldOwner(String field, Map<String, Map<String, ColumnValObj>> tableCols) {
        for (Map.Entry<String, Map<String, ColumnValObj>> entry : tableCols.entrySet()) {
            if (entry.getValue().containsKey(field.toLowerCase(Locale.ROOT))) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * 表名白名单校验(按表自身 schema 查 listTables)
     */
    private String assertTable(DataSourceConnector connector, DataSourceConfig dsConfig, String table, String schema) {
        Assert.notBlank(table, new SilentException("JOIN 关联表名不能为空"));
        Set<String> allowed = new HashSet<>();
        for (String t : connector.listTables(dsConfig, schema)) {
            allowed.add(t.toLowerCase(Locale.ROOT));
        }
        Assert.isTrue(allowed.contains(table.toLowerCase(Locale.ROOT)),
                new SilentException("表不存在或不可见: " + table));
        return table;
    }

    /**
     * describeTable 转 字段名(小写) -> ColumnValObj
     */
    private Map<String, ColumnValObj> describeAsMap(DataSourceConnector connector, DataSourceConfig config,
                                                     String schema, String table) {
        TableSchemaValObj schemaValObj = connector.describeTable(config, schema, table);
        Map<String, ColumnValObj> map = new LinkedHashMap<>();
        for (ColumnValObj c : schemaValObj.getColumns()) {
            map.put(c.getName().toLowerCase(Locale.ROOT), c);
        }
        return map;
    }

    /**
     * 限定表名 schema.table(加引号)
     */
    private String qualified(String schema, String table, DataSourceConnector connector) {
        if (schema == null || schema.isBlank()) {
            return connector.quoteIdentifier(table);
        }
        return connector.quoteIdentifier(schema) + "." + connector.quoteIdentifier(table);
    }
}
