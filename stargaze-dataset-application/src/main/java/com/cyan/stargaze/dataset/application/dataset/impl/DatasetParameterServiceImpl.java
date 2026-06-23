package com.cyan.stargaze.dataset.application.dataset.impl;

import com.cyan.arch.common.api.Assert;
import com.cyan.arch.common.api.SilentException;
import com.cyan.stargaze.dataset.application.dataset.DatasetParameterService;
import com.cyan.stargaze.dataset.application.dataset.cmd.DatasetParameterCmd;
import com.cyan.stargaze.dataset.application.dataset.convert.DatasetAuxiliaryAppConvert;
import com.cyan.stargaze.dataset.domain.dataset.DatasetParameter;
import com.cyan.stargaze.dataset.domain.dataset.repository.DatasetParameterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 数据集参数字段应用服务实现。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class DatasetParameterServiceImpl implements DatasetParameterService {

    private final DatasetParameterRepository repository;
    private final DatasetAuxiliaryAppConvert convert;

    @Override
    @Transactional
    public DatasetParameter create(DatasetParameterCmd cmd) {
        DatasetParameter parameter = convert.toParameter(cmd);
        parameter.validate();
        return repository.save(parameter);
    }

    @Override
    @Transactional
    public DatasetParameter update(DatasetParameterCmd cmd) {
        DatasetParameter existing = repository.findById(cmd.getId());
        Assert.notNull(existing, new SilentException("参数字段不存在"));
        DatasetParameter parameter = convert.toParameter(cmd);
        parameter.setId(existing.getId());
        parameter.validate();
        return repository.update(parameter);
    }

    @Override
    @Transactional
    public void delete(String id) {
        repository.deleteById(id);
    }

    @Override
    public List<DatasetParameter> listByDatasetId(String datasetId) {
        return repository.listByDatasetId(datasetId);
    }
}
