package com.cyan.stargaze.dataset.infra.persistence.dataset.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cyan.stargaze.dataset.domain.dataset.DatasetParameter;
import com.cyan.stargaze.dataset.domain.dataset.repository.DatasetParameterRepository;
import com.cyan.stargaze.dataset.infra.persistence.dataset.convert.DatasetParameterInfraConvert;
import com.cyan.stargaze.dataset.infra.persistence.dataset.dos.DatasetParameterDO;
import com.cyan.stargaze.dataset.infra.persistence.dataset.mappers.DatasetParameterMapper;
import com.cyan.stargaze.dataset.infra.util.IdUtil;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 数据集参数字段仓储实现。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Repository
public class DatasetParameterRepositoryImpl implements DatasetParameterRepository {

    private final DatasetParameterMapper mapper;
    private final DatasetParameterInfraConvert convert;

    public DatasetParameterRepositoryImpl(DatasetParameterMapper mapper, DatasetParameterInfraConvert convert) {
        this.mapper = mapper;
        this.convert = convert;
    }

    @Override
    public DatasetParameter findById(String id) {
        DatasetParameterDO parameterDO = mapper.selectById(IdUtil.toLong(id));
        return parameterDO == null ? null : convert.toParameter(parameterDO);
    }

    @Override
    public List<DatasetParameter> listByDatasetId(String datasetId) {
        LambdaQueryWrapper<DatasetParameterDO> wrapper = new LambdaQueryWrapper<DatasetParameterDO>()
                .eq(DatasetParameterDO::getDatasetId, IdUtil.toLong(datasetId));
        return mapper.selectList(wrapper).stream()
                .map(convert::toParameter)
                .toList();
    }

    @Override
    public DatasetParameter save(DatasetParameter parameter) {
        DatasetParameterDO parameterDO = convert.toParameterDO(parameter);
        mapper.insert(parameterDO);
        return findById(IdUtil.toString(parameterDO.getId()));
    }

    @Override
    public DatasetParameter update(DatasetParameter parameter) {
        DatasetParameterDO parameterDO = convert.toParameterDO(parameter);
        mapper.updateById(parameterDO);
        return findById(IdUtil.toString(parameterDO.getId()));
    }

    @Override
    public void deleteById(String id) {
        mapper.deleteById(IdUtil.toLong(id));
    }
}
