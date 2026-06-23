package com.cyan.stargaze.dataset.domain.dataset.repository;

import com.cyan.stargaze.dataset.domain.dataset.DatasetHierarchy;

import java.util.List;

/**
 * 数据集维度层级仓储接口。
 *
 * @author cy.Y
 * @since 1.0.0
 */
public interface DatasetHierarchyRepository {

    /**
     * 根据 ID 查询
     */
    DatasetHierarchy findById(String id);

    /**
     * 查询数据集全部层级
     */
    List<DatasetHierarchy> listByDatasetId(String datasetId);

    /**
     * 保存
     */
    DatasetHierarchy save(DatasetHierarchy hierarchy);

    /**
     * 更新
     */
    DatasetHierarchy update(DatasetHierarchy hierarchy);

    /**
     * 逻辑删除
     */
    void deleteById(String id);
}
