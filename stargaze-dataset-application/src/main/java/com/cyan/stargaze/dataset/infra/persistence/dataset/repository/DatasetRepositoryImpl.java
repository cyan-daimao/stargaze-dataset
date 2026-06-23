package com.cyan.stargaze.dataset.infra.persistence.dataset.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cyan.stargaze.dataset.domain.dataset.Dataset;
import com.cyan.stargaze.dataset.domain.dataset.DatasetField;
import com.cyan.stargaze.dataset.domain.dataset.query.DatasetListQuery;
import com.cyan.stargaze.dataset.domain.dataset.repository.DatasetRepository;
import com.cyan.stargaze.dataset.infra.persistence.dataset.convert.DatasetFieldInfraConvert;
import com.cyan.stargaze.dataset.infra.persistence.dataset.convert.DatasetInfraConvert;
import com.cyan.stargaze.dataset.infra.persistence.dataset.dos.DatasetDO;
import com.cyan.stargaze.dataset.infra.persistence.dataset.dos.DatasetFieldDO;
import com.cyan.stargaze.dataset.infra.persistence.dataset.mappers.DatasetFieldMapper;
import com.cyan.stargaze.dataset.infra.persistence.dataset.mappers.DatasetMapper;
import com.cyan.stargaze.dataset.infra.util.IdUtil;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 数据集仓储实现(聚合根:主表 + 字段列表)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Repository
public class DatasetRepositoryImpl implements DatasetRepository {

    private final DatasetMapper datasetMapper;
    private final DatasetFieldMapper datasetFieldMapper;
    private final DatasetInfraConvert convert;
    private final DatasetFieldInfraConvert fieldConvert;

    public DatasetRepositoryImpl(DatasetMapper datasetMapper,
                                 DatasetFieldMapper datasetFieldMapper,
                                 DatasetInfraConvert convert,
                                 DatasetFieldInfraConvert fieldConvert) {
        this.datasetMapper = datasetMapper;
        this.datasetFieldMapper = datasetFieldMapper;
        this.convert = convert;
        this.fieldConvert = fieldConvert;
    }

    @Override
    public Dataset findById(String id) {
        DatasetDO datasetDO = datasetMapper.selectById(IdUtil.toLong(id));
        if (datasetDO == null) {
            return null;
        }
        Dataset dataset = convert.toDataset(datasetDO);
        dataset.setFields(loadFields(IdUtil.toString(datasetDO.getId())));
        return dataset;
    }

    @Override
    public List<Dataset> list(DatasetListQuery query) {
        query = query == null ? new DatasetListQuery() : query;
        LambdaQueryWrapper<DatasetDO> wrapper = new LambdaQueryWrapper<DatasetDO>()
                .eq(StringUtils.isNotBlank(query.getWorkspaceId()),
                        DatasetDO::getWorkspaceId, IdUtil.toLong(query.getWorkspaceId()))
                .like(StringUtils.isNotBlank(query.getName()),
                        DatasetDO::getName, query.getName())
                .eq(query.getSourceType() != null, DatasetDO::getSourceType, query.getSourceType())
                .eq(StringUtils.isNotBlank(query.getDataSourceId()),
                        DatasetDO::getDataSourceId, IdUtil.toLong(query.getDataSourceId()))
                .orderByDesc(DatasetDO::getCreatedAt);
        List<DatasetDO> list = datasetMapper.selectList(wrapper);
        return Optional.ofNullable(list).orElse(List.of()).stream()
                .map(convert::toDataset)
                .toList();
    }

    @Override
    public Dataset findByName(String workspaceId, String name) {
        LambdaQueryWrapper<DatasetDO> wrapper = new LambdaQueryWrapper<DatasetDO>()
                .eq(DatasetDO::getWorkspaceId, IdUtil.toLong(workspaceId))
                .eq(DatasetDO::getName, name);
        DatasetDO datasetDO = datasetMapper.selectOne(wrapper);
        return datasetDO == null ? null : convert.toDataset(datasetDO);
    }

    @Override
    @Transactional
    public Dataset save(Dataset dataset) {
        DatasetDO datasetDO = convert.toDatasetDO(dataset);
        datasetMapper.insert(datasetDO);
        String datasetId = IdUtil.toString(datasetDO.getId());
        saveFields(datasetId, dataset.getFields());
        return findById(datasetId);
    }

    @Override
    @Transactional
    public Dataset update(Dataset dataset) {
        DatasetDO datasetDO = convert.toDatasetDO(dataset);
        datasetMapper.updateById(datasetDO);
        String datasetId = IdUtil.toString(datasetDO.getId());
        // 全量重建字段
        deleteFields(datasetId);
        saveFields(datasetId, dataset.getFields());
        return findById(datasetId);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        datasetMapper.deleteById(IdUtil.toLong(id));
        deleteFields(id);
    }

    @Override
    public boolean existsById(String id) {
        return datasetMapper.selectById(IdUtil.toLong(id)) != null;
    }

    private List<DatasetField> loadFields(String datasetId) {
        LambdaQueryWrapper<DatasetFieldDO> wrapper = new LambdaQueryWrapper<DatasetFieldDO>()
                .eq(DatasetFieldDO::getDatasetId, IdUtil.toLong(datasetId))
                .orderByAsc(DatasetFieldDO::getOrd);
        return datasetFieldMapper.selectList(wrapper).stream()
                .map(fieldConvert::toDatasetField)
                .toList();
    }

    private void saveFields(String datasetId, List<DatasetField> fields) {
        if (fields == null || fields.isEmpty()) {
            return;
        }
        List<DatasetFieldDO> fieldDOs = fields.stream().peek(f -> f.setDatasetId(datasetId))
                .map(fieldConvert::toDatasetFieldDO)
                .toList();
        fieldDOs.forEach(datasetFieldMapper::insert);
    }

    private void deleteFields(String datasetId) {
        LambdaQueryWrapper<DatasetFieldDO> wrapper = new LambdaQueryWrapper<DatasetFieldDO>()
                .eq(DatasetFieldDO::getDatasetId, IdUtil.toLong(datasetId));
        datasetFieldMapper.delete(wrapper);
    }
}
