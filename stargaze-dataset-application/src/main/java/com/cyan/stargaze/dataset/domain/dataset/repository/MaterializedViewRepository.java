package com.cyan.stargaze.dataset.domain.dataset.repository;

import com.cyan.stargaze.dataset.domain.dataset.MaterializedView;

import java.util.List;

/**
 * 物化加速配置仓储接口。
 *
 * @author cy.Y
 * @since 1.0.0
 */
public interface MaterializedViewRepository {

    /**
     * 根据 ID 查询
     */
    MaterializedView findById(String id);

    /**
     * 查询数据集全部物化加速配置
     */
    List<MaterializedView> listByDatasetId(String datasetId);

    /**
     * 查询数据集启用的物化加速配置
     */
    MaterializedView findEnabledByDatasetId(String datasetId);

    /**
     * 保存
     */
    MaterializedView save(MaterializedView view);

    /**
     * 更新
     */
    MaterializedView update(MaterializedView view);

    /**
     * 逻辑删除
     */
    void deleteById(String id);
}
