package com.cyan.stargaze.dataset.infra.persistence.datasource.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cyan.stargaze.dataset.domain.datasource.DataSource;
import com.cyan.stargaze.dataset.domain.datasource.query.DataSourceListQuery;
import com.cyan.stargaze.dataset.domain.datasource.repository.DataSourceRepository;
import com.cyan.stargaze.dataset.infra.persistence.datasource.convert.DataSourceInfraConvert;
import com.cyan.stargaze.dataset.infra.persistence.datasource.dos.DataSourceDO;
import com.cyan.stargaze.dataset.infra.persistence.datasource.mappers.DataSourceMapper;
import com.cyan.stargaze.dataset.infra.util.IdUtil;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 数据源仓储实现。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Repository
public class DataSourceRepositoryImpl implements DataSourceRepository {

    private final DataSourceMapper dataSourceMapper;
    private final DataSourceInfraConvert convert;

    public DataSourceRepositoryImpl(DataSourceMapper dataSourceMapper, DataSourceInfraConvert convert) {
        this.dataSourceMapper = dataSourceMapper;
        this.convert = convert;
    }

    @Override
    public DataSource findById(String id) {
        DataSourceDO dataSourceDO = dataSourceMapper.selectById(IdUtil.toLong(id));
        return dataSourceDO == null ? null : convert.toDataSource(dataSourceDO);
    }

    @Override
    public List<DataSource> findByIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        List<Long> longIds = ids.stream().map(IdUtil::toLong).filter(java.util.Objects::nonNull).toList();
        if (longIds.isEmpty()) {
            return List.of();
        }
        List<DataSourceDO> list = dataSourceMapper.selectBatchIds(longIds);
        return java.util.Optional.ofNullable(list).orElse(java.util.List.of()).stream()
                .map(convert::toDataSource)
                .toList();
    }

    @Override
    public List<DataSource> list(DataSourceListQuery query) {
        query = query == null ? new DataSourceListQuery() : query;
        LambdaQueryWrapper<DataSourceDO> wrapper = new LambdaQueryWrapper<DataSourceDO>()
                .eq(StringUtils.isNotBlank(query.getWorkspaceId()),
                        DataSourceDO::getWorkspaceId, IdUtil.toLong(query.getWorkspaceId()))
                .like(StringUtils.isNotBlank(query.getName()),
                        DataSourceDO::getName, query.getName())
                .eq(query.getType() != null, DataSourceDO::getType, query.getType())
                .orderByDesc(DataSourceDO::getCreatedAt);
        List<DataSourceDO> list = dataSourceMapper.selectList(wrapper);
        return Optional.ofNullable(list).orElse(List.of()).stream()
                .map(convert::toDataSource)
                .toList();
    }

    @Override
    public DataSource findByName(String workspaceId, String name) {
        LambdaQueryWrapper<DataSourceDO> wrapper = new LambdaQueryWrapper<DataSourceDO>()
                .eq(DataSourceDO::getWorkspaceId, IdUtil.toLong(workspaceId))
                .eq(DataSourceDO::getName, name);
        DataSourceDO dataSourceDO = dataSourceMapper.selectOne(wrapper);
        return dataSourceDO == null ? null : convert.toDataSource(dataSourceDO);
    }

    @Override
    public DataSource save(DataSource dataSource) {
        DataSourceDO dataSourceDO = convert.toDataSourceDO(dataSource);
        dataSourceMapper.insert(dataSourceDO);
        return findById(IdUtil.toString(dataSourceDO.getId()));
    }

    @Override
    public DataSource update(DataSource dataSource) {
        DataSourceDO dataSourceDO = convert.toDataSourceDO(dataSource);
        dataSourceMapper.updateById(dataSourceDO);
        return findById(IdUtil.toString(dataSourceDO.getId()));
    }

    @Override
    public void deleteById(String id) {
        dataSourceMapper.deleteById(IdUtil.toLong(id));
    }
}
