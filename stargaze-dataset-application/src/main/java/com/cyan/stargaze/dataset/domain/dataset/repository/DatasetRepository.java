package com.cyan.stargaze.dataset.domain.dataset.repository;

import com.cyan.stargaze.dataset.domain.dataset.Dataset;
import com.cyan.stargaze.dataset.domain.dataset.query.DatasetListQuery;

import java.util.List;

/**
 * 数据集仓储接口(聚合根,字段随主表一起持久化)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
public interface DatasetRepository {

    /**
     * 根据 ID 查询(含字段列表)
     */
    Dataset findById(String id);

    /**
     * 列表查询(不含字段明细)
     */
    List<Dataset> list(DatasetListQuery query);

    /**
     * 按空间+名称查询(唯一性校验)
     */
    Dataset findByName(String workspaceId, String name);

    /**
     * 保存(主表 + 字段列表)
     */
    Dataset save(Dataset dataset);

    /**
     * 更新(主表 + 全量重建字段)
     */
    Dataset update(Dataset dataset);

    /**
     * 逻辑删除(连带字段)
     */
    void deleteById(String id);

    /**
     * 是否存在且可用
     */
    boolean existsById(String id);
}
