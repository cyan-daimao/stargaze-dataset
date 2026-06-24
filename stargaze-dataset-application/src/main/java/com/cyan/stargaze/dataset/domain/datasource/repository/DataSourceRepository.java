package com.cyan.stargaze.dataset.domain.datasource.repository;

import com.cyan.stargaze.dataset.domain.datasource.DataSource;
import com.cyan.stargaze.dataset.domain.datasource.query.DataSourceListQuery;

import java.util.List;

/**
 * 数据源仓储接口(domain 端口,infra 实现)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
public interface DataSourceRepository {

    /**
     * 根据 ID 查询
     */
    DataSource findById(String id);

    /**
     * 批量根据 ID 查询(列表组装 datasource_name 用)
     */
    List<DataSource> findByIds(List<String> ids);

    /**
     * 列表查询
     */
    List<DataSource> list(DataSourceListQuery query);

    /**
     * 按空间+名称查询(唯一性校验)
     */
    DataSource findByName(String workspaceId, String name);

    /**
     * 保存
     */
    DataSource save(DataSource dataSource);

    /**
     * 更新
     */
    DataSource update(DataSource dataSource);

    /**
     * 逻辑删除
     */
    void deleteById(String id);
}
