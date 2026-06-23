package com.cyan.stargaze.dataset.infra.persistence.dataset.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cyan.stargaze.dataset.domain.dataset.DatasetField;
import com.cyan.stargaze.dataset.domain.dataset.repository.DatasetFieldRepository;
import com.cyan.stargaze.dataset.infra.persistence.dataset.convert.DatasetFieldInfraConvert;
import com.cyan.stargaze.dataset.infra.persistence.dataset.dos.DatasetFieldDO;
import com.cyan.stargaze.dataset.infra.persistence.dataset.mappers.DatasetFieldMapper;
import com.cyan.stargaze.dataset.infra.util.IdUtil;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 数据集字段仓储实现。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Repository
public class DatasetFieldRepositoryImpl implements DatasetFieldRepository {

    private final DatasetFieldMapper datasetFieldMapper;
    private final DatasetFieldInfraConvert convert;

    public DatasetFieldRepositoryImpl(DatasetFieldMapper datasetFieldMapper, DatasetFieldInfraConvert convert) {
        this.datasetFieldMapper = datasetFieldMapper;
        this.convert = convert;
    }

    @Override
    public DatasetField findById(String id) {
        DatasetFieldDO fieldDO = datasetFieldMapper.selectById(IdUtil.toLong(id));
        return fieldDO == null ? null : convert.toDatasetField(fieldDO);
    }

    @Override
    public List<DatasetField> listByDatasetId(String datasetId) {
        LambdaQueryWrapper<DatasetFieldDO> wrapper = new LambdaQueryWrapper<DatasetFieldDO>()
                .eq(DatasetFieldDO::getDatasetId, IdUtil.toLong(datasetId))
                .orderByAsc(DatasetFieldDO::getOrd);
        return datasetFieldMapper.selectList(wrapper).stream()
                .map(convert::toDatasetField)
                .toList();
    }

    @Override
    public void saveAll(String datasetId, List<DatasetField> fields) {
        if (fields == null || fields.isEmpty()) {
            return;
        }
        fields.stream().peek(f -> f.setDatasetId(datasetId))
                .map(convert::toDatasetFieldDO)
                .forEach(datasetFieldMapper::insert);
    }

    @Override
    public void deleteByDatasetId(String datasetId) {
        LambdaQueryWrapper<DatasetFieldDO> wrapper = new LambdaQueryWrapper<DatasetFieldDO>()
                .eq(DatasetFieldDO::getDatasetId, IdUtil.toLong(datasetId));
        datasetFieldMapper.delete(wrapper);
    }
}
