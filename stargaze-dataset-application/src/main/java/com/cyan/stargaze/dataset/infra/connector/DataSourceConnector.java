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
}
