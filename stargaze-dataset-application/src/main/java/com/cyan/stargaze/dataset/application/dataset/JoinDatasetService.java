package com.cyan.stargaze.dataset.application.dataset;

import com.cyan.stargaze.dataset.application.dataset.bo.DatasetFieldBO;
import com.cyan.stargaze.dataset.domain.dataset.config.JoinConfig;
import com.cyan.stargaze.dataset.domain.datasource.valobj.TableSampleValObj;

import java.util.List;

/**
 * JOIN 数据集应用服务:字段解析 / 预览。
 *
 * @author cy.Y
 * @since 1.0.0
 */
public interface JoinDatasetService {

    /**
     * 解析 JOIN 为数据集字段列表
     */
    List<DatasetFieldBO> resolveFields(String datasourceId, JoinConfig config);

    /**
     * 预览 JOIN 数据
     */
    TableSampleValObj preview(String datasourceId, JoinConfig config, int limit);
}
