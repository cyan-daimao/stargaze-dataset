package com.cyan.stargaze.dataset.infra.connector;

import com.cyan.stargaze.dataset.domain.datasource.valobj.DataSourceConfig;
import com.cyan.stargaze.dataset.enums.DatasourceType;

import java.util.List;

/**
 * 数据源适配器接口(插件化)。
 * <p>
 * 实现本接口即可接入新数据源;各实现为独立类,由 {@link DataSourceConnectorFactory} 按 type 分发。
 *
 * @author cy.Y
 * @since 1.0.0
 */
public interface DataSourceConnector {

    /**
     * 适配的数据源类型
     */
    DatasourceType supportType();

    /**
     * 测试连接(连通性 + 查询权限)
     */
    void testConnection(DataSourceConfig config);

    /**
     * 列出库/schema
     */
    List<String> listSchemas(DataSourceConfig config);

    /**
     * 列出表
     */
    List<String> listTables(DataSourceConfig config, String schema);

    /**
     * 描述表结构(字段列表)
     */
    com.cyan.stargaze.dataset.domain.datasource.valobj.TableSchemaValObj describeTable(
            DataSourceConfig config, String schema, String tableName);

    /**
     * 采样表数据
     */
    com.cyan.stargaze.dataset.domain.datasource.valobj.TableSampleValObj sampleTable(
            DataSourceConfig config, String schema, String tableName, int limit);

    /**
     * 解析自定义 SQL 的字段结构(不取数据)。
     * <p>
     * 包装为 select * from (&lt;sql&gt;) __t__ where 1 = 0 取 ResultSetMetaData。
     *
     * @param config 连接配置
     * @param schema 库/schema(执行前设置连接 schema,可空)
     * @param sql    用户自定义 SELECT SQL(调用方须先做安全校验)
     */
    com.cyan.stargaze.dataset.domain.datasource.valobj.TableSchemaValObj describeSql(
            DataSourceConfig config, String schema, String sql);

    /**
     * 采样自定义 SQL 的数据。
     *
     * @param limit 最大返回行数
     */
    com.cyan.stargaze.dataset.domain.datasource.valobj.TableSampleValObj sampleSql(
            DataSourceConfig config, String schema, String sql, int limit);

    /**
     * 富列出表(含表注释/字段数/行数估算,供前端表选择器)。
     *
     * @param schema  库/schema,可空(取当前 schema)
     * @param keyword 表名关键词,可空
     */
    java.util.List<com.cyan.stargaze.dataset.domain.datasource.valobj.TableMetaValObj> listTablesRich(
            DataSourceConfig config, String schema, String keyword);

    /**
     * 统计表行数(best-effort,可能为估算值)。
     */
    long countTable(DataSourceConfig config, String schema, String tableName);

    /**
     * 标识符加引号(防 SQL 注入,方言相关:MySQL 反引号,PG 双引号)。
     */
    String quoteIdentifier(String name);
}
