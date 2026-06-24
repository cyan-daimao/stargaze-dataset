package com.cyan.stargaze.dataset.domain.dataset.repository;

import com.cyan.stargaze.dataset.domain.dataset.DatasetFile;

/**
 * 数据集文件仓储接口(domain 端口,infra 实现)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
public interface DatasetFileRepository {

    /**
     * 根据 ID 查询
     */
    DatasetFile findById(String id);

    /**
     * 按对象 key 查询
     */
    DatasetFile findByObjectKey(String objectKey);

    /**
     * 对象 key 是否存在
     */
    boolean existsByObjectKey(String objectKey);

    /**
     * 保存
     */
    DatasetFile save(DatasetFile datasetFile);

    /**
     * 逻辑删除
     */
    void deleteById(String id);
}
