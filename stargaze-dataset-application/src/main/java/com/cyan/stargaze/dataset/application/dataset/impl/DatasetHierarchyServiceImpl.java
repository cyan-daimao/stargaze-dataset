package com.cyan.stargaze.dataset.application.dataset.impl;

import com.cyan.arch.common.api.Assert;
import com.cyan.arch.common.api.SilentException;
import com.cyan.stargaze.dataset.application.dataset.DatasetHierarchyService;
import com.cyan.stargaze.dataset.application.dataset.bo.DatasetHierarchyBO;
import com.cyan.stargaze.dataset.application.dataset.cmd.DatasetHierarchyCmd;
import com.cyan.stargaze.dataset.application.dataset.convert.DatasetAuxiliaryAppConvert;
import com.cyan.stargaze.dataset.domain.dataset.DatasetHierarchy;
import com.cyan.stargaze.dataset.domain.dataset.repository.DatasetHierarchyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 数据集维度层级应用服务实现。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class DatasetHierarchyServiceImpl implements DatasetHierarchyService {

    private final DatasetHierarchyRepository repository;
    private final DatasetAuxiliaryAppConvert convert;

    @Override
    @Transactional
    public DatasetHierarchyBO create(DatasetHierarchyCmd cmd) {
        DatasetHierarchy hierarchy = convert.toHierarchy(cmd);
        hierarchy = hierarchy.save(repository);
        return convert.toHierarchyBO(hierarchy);
    }

    @Override
    @Transactional
    public DatasetHierarchyBO update(DatasetHierarchyCmd cmd) {
        DatasetHierarchy existing = repository.findById(cmd.getId());
        Assert.notNull(existing, new SilentException("维度层级不存在"));
        DatasetHierarchy hierarchy = convert.toHierarchy(cmd);
        hierarchy.setId(existing.getId());
        hierarchy = hierarchy.update(repository);
        return convert.toHierarchyBO(hierarchy);
    }

    @Override
    @Transactional
    public void delete(String id) {
        DatasetHierarchy hierarchy = repository.findById(id);
        Assert.notNull(hierarchy, new SilentException("维度层级不存在"));
        hierarchy.delete(repository);
    }

    @Override
    public List<DatasetHierarchyBO> listByDatasetId(String datasetId) {
        return convert.toHierarchyBOList(repository.listByDatasetId(datasetId));
    }
}
