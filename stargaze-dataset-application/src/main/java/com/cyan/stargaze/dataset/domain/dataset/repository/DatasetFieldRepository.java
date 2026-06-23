package com.cyan.stargaze.dataset.domain.dataset.repository;

import com.cyan.stargaze.dataset.domain.dataset.DatasetField;

import java.util.List;

/**
 * 数据集字段仓储接口(供 metric 单独解析字段)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
public interface DatasetFieldRepository {

    /**
     * 根据字段 ID 查询
     */
    DatasetField findById(String id);

    /**
     * 查询数据集全部字段(按 ord 排序)
     */
    List<DatasetField> listByDatasetId(String datasetId);

    /**
     * 批量保存(重建数据集字段)
     */
    void saveAll(String datasetId, List<DatasetField> fields);

    /**
     * 逻辑删除数据集全部字段
     */
    void deleteByDatasetId(String datasetId);
}
