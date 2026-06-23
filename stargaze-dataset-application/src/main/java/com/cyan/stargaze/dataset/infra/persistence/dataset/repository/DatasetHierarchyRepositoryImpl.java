package com.cyan.stargaze.dataset.infra.persistence.dataset.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cyan.stargaze.dataset.domain.dataset.DatasetHierarchy;
import com.cyan.stargaze.dataset.domain.dataset.repository.DatasetHierarchyRepository;
import com.cyan.stargaze.dataset.infra.persistence.dataset.convert.DatasetHierarchyInfraConvert;
import com.cyan.stargaze.dataset.infra.persistence.dataset.dos.DatasetHierarchyDO;
import com.cyan.stargaze.dataset.infra.persistence.dataset.mappers.DatasetHierarchyMapper;
import com.cyan.stargaze.dataset.infra.util.IdUtil;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 数据集维度层级仓储实现。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Repository
public class DatasetHierarchyRepositoryImpl implements DatasetHierarchyRepository {

    private final DatasetHierarchyMapper mapper;
    private final DatasetHierarchyInfraConvert convert;

    public DatasetHierarchyRepositoryImpl(DatasetHierarchyMapper mapper, DatasetHierarchyInfraConvert convert) {
        this.mapper = mapper;
        this.convert = convert;
    }

    @Override
    public DatasetHierarchy findById(String id) {
        DatasetHierarchyDO hierarchyDO = mapper.selectById(IdUtil.toLong(id));
        return hierarchyDO == null ? null : convert.toHierarchy(hierarchyDO);
    }

    @Override
    public List<DatasetHierarchy> listByDatasetId(String datasetId) {
        LambdaQueryWrapper<DatasetHierarchyDO> wrapper = new LambdaQueryWrapper<DatasetHierarchyDO>()
                .eq(DatasetHierarchyDO::getDatasetId, IdUtil.toLong(datasetId));
        return mapper.selectList(wrapper).stream()
                .map(convert::toHierarchy)
                .toList();
    }

    @Override
    public DatasetHierarchy save(DatasetHierarchy hierarchy) {
        DatasetHierarchyDO hierarchyDO = convert.toHierarchyDO(hierarchy);
        mapper.insert(hierarchyDO);
        return findById(IdUtil.toString(hierarchyDO.getId()));
    }

    @Override
    public DatasetHierarchy update(DatasetHierarchy hierarchy) {
        DatasetHierarchyDO hierarchyDO = convert.toHierarchyDO(hierarchy);
        mapper.updateById(hierarchyDO);
        return findById(IdUtil.toString(hierarchyDO.getId()));
    }

    @Override
    public void deleteById(String id) {
        mapper.deleteById(IdUtil.toLong(id));
    }
}
