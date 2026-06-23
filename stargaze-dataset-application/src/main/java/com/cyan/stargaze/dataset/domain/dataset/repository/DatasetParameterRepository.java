package com.cyan.stargaze.dataset.domain.dataset.repository;

import com.cyan.stargaze.dataset.domain.dataset.DatasetParameter;

import java.util.List;

/**
 * 数据集参数字段仓储接口。
 *
 * @author cy.Y
 * @since 1.0.0
 */
public interface DatasetParameterRepository {

    /**
     * 根据 ID 查询
     */
    DatasetParameter findById(String id);

    /**
     * 查询数据集全部参数
     */
    List<DatasetParameter> listByDatasetId(String datasetId);

    /**
     * 保存
     */
    DatasetParameter save(DatasetParameter parameter);

    /**
     * 更新
     */
    DatasetParameter update(DatasetParameter parameter);

    /**
     * 逻辑删除
     */
    void deleteById(String id);
}
