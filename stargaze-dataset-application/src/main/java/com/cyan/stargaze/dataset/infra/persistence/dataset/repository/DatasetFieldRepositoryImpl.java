package com.cyan.stargaze.dataset.infra.persistence.dataset.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cyan.stargaze.dataset.domain.dataset.DatasetField;
import com.cyan.stargaze.dataset.domain.dataset.repository.DatasetFieldRepository;
import com.cyan.stargaze.dataset.domain.dataset.valobj.FieldCountStat;
import com.cyan.stargaze.dataset.infra.persistence.dataset.convert.DatasetFieldInfraConvert;
import com.cyan.stargaze.dataset.infra.persistence.dataset.dos.DatasetFieldDO;
import com.cyan.stargaze.dataset.infra.persistence.dataset.mappers.DatasetFieldMapper;
import com.cyan.stargaze.dataset.infra.util.IdUtil;
import com.cyan.stargaze.dataset.enums.FieldType;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public List<FieldCountStat> countByDatasetIds(List<String> datasetIds) {
        if (datasetIds == null || datasetIds.isEmpty()) {
            return List.of();
        }
        List<Long> ids = datasetIds.stream().map(IdUtil::toLong).toList();
        LambdaQueryWrapper<DatasetFieldDO> wrapper = new LambdaQueryWrapper<DatasetFieldDO>()
                .in(DatasetFieldDO::getDatasetId, ids);
        List<DatasetFieldDO> all = datasetFieldMapper.selectList(wrapper);
        // 按 datasetId 分组,再按 fieldType 统计
        Map<Long, List<DatasetFieldDO>> grouped = all.stream()
                .collect(Collectors.groupingBy(DatasetFieldDO::getDatasetId));
        List<FieldCountStat> result = new ArrayList<>();
        for (Map.Entry<Long, List<DatasetFieldDO>> entry : grouped.entrySet()) {
            List<DatasetFieldDO> fields = entry.getValue();
            int dim = (int) fields.stream().filter(f -> FieldType.DIMENSION == f.getFieldType()).count();
            int measure = (int) fields.stream().filter(f -> FieldType.MEASURE == f.getFieldType()).count();
            result.add(new FieldCountStat()
                    .setDatasetId(IdUtil.toString(entry.getKey()))
                    .setTotal(fields.size())
                    .setDimension(dim)
                    .setMeasure(measure));
        }
        return result;
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
