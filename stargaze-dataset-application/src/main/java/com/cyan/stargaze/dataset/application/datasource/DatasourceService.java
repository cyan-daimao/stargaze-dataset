package com.cyan.stargaze.dataset.application.datasource;

import com.cyan.stargaze.dataset.application.datasource.bo.DatasourceBO;
import com.cyan.stargaze.dataset.application.datasource.cmd.DatasourceCmd;
import com.cyan.stargaze.dataset.domain.datasource.query.DataSourceListQuery;
import com.cyan.stargaze.dataset.domain.datasource.valobj.DatabaseValObj;
import com.cyan.stargaze.dataset.domain.datasource.valobj.TableSampleValObj;
import com.cyan.stargaze.dataset.domain.datasource.valobj.TableSchemaValObj;

import java.util.List;

/**
 * 数据源应用服务。
 *
 * @author cy.Y
 * @since 1.0.0
 */
public interface DatasourceService {

    /**
     * 创建数据源
     */
    DatasourceBO create(DatasourceCmd cmd);

    /**
     * 更新数据源
     */
    DatasourceBO update(DatasourceCmd cmd);

    /**
     * 列表查询
     */
    List<DatasourceBO> list(DataSourceListQuery query);

    /**
     * 详情
     */
    DatasourceBO findById(String id);

    /**
     * 删除
     */
    void delete(String id);

    /**
     * 测试连接
     */
    void testConnection(String id);

    /**
     * 探查:列出库/schema
     */
    List<DatabaseValObj> listSchemas(String id);

    /**
     * 探查:列出表
     */
    List<String> listTables(String id, String schema);

    /**
     * 探查:富列出表(含表注释/字段数/行数)
     */
    List<com.cyan.stargaze.dataset.domain.datasource.valobj.TableMetaValObj> listTablesRich(
            String id, String schema, String keyword);

    /**
     * 探查:表结构
     */
    TableSchemaValObj describeTable(String id, String schema, String tableName);

    /**
     * 探查:采样表数据
     */
    TableSampleValObj sampleTable(String id, String schema, String tableName, int limit);
}
