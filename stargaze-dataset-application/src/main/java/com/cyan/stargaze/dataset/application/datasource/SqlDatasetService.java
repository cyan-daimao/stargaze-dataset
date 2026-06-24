package com.cyan.stargaze.dataset.application.datasource;

import com.cyan.stargaze.dataset.application.dataset.bo.DatasetFieldBO;
import com.cyan.stargaze.dataset.application.dataset.bo.SqlPreviewBO;
import com.cyan.stargaze.dataset.application.dataset.cmd.SqlPreviewCmd;
import com.cyan.stargaze.dataset.domain.datasource.valobj.TableSampleValObj;
import com.cyan.stargaze.dataset.domain.datasource.valobj.TableSchemaValObj;

import java.util.List;

/**
 * SQL 数据集应用服务:预览 / schema 解析 / 采样 / 字段解析。
 *
 * @author cy.Y
 * @since 1.0.0
 */
public interface SqlDatasetService {

    /**
     * SQL 预览(校验 + 解析列/类型 + 采样,返回 columns/rows/耗时/截断标记)
     */
    SqlPreviewBO preview(SqlPreviewCmd cmd);

    /**
     * 解析 SQL 字段结构(连库取 metadata)
     */
    TableSchemaValObj schema(String datasourceId, String sql, String schema);

    /**
     * 采样 SQL 数据
     */
    TableSampleValObj sample(String datasourceId, String sql, String schema, int limit);

    /**
     * 解析 SQL 为数据集字段列表
     */
    List<DatasetFieldBO> resolveFields(String datasourceId, String sql, String schema);
}
