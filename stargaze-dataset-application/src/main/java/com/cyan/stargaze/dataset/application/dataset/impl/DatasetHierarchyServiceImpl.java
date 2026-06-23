package com.cyan.stargaze.dataset.application.dataset.impl;

import com.cyan.arch.common.api.Assert;
import com.cyan.arch.common.api.SilentException;
import com.cyan.stargaze.dataset.application.dataset.DatasetHierarchyService;
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
    public DatasetHierarchy create(DatasetHierarchyCmd cmd) {
        DatasetHierarchy hierarchy = convert.toHierarchy(cmd);
        return repository.save(hierarchy);
    }

    @Override
    @Transactional
    public DatasetHierarchy update(DatasetHierarchyCmd cmd) {
        DatasetHierarchy existing = repository.findById(cmd.getId());
        Assert.notNull(existing, new SilentException("维度层级不存在"));
        DatasetHierarchy hierarchy = convert.toHierarchy(cmd);
        hierarchy.setId(existing.getId());
        return repository.update(hierarchy);
    }

    @Override
    @Transactional
    public void delete(String id) {
        repository.deleteById(id);
    }

    @Override
    public List<DatasetHierarchy> listByDatasetId(String datasetId) {
        return repository.listByDatasetId(datasetId);
    }
}
