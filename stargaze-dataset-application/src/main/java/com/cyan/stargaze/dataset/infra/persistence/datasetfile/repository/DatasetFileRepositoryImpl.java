package com.cyan.stargaze.dataset.infra.persistence.datasetfile.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cyan.stargaze.dataset.domain.dataset.DatasetFile;
import com.cyan.stargaze.dataset.domain.dataset.repository.DatasetFileRepository;
import com.cyan.stargaze.dataset.infra.persistence.datasetfile.convert.DatasetFileInfraConvert;
import com.cyan.stargaze.dataset.infra.persistence.datasetfile.dos.DatasetFileDO;
import com.cyan.stargaze.dataset.infra.persistence.datasetfile.mappers.DatasetFileMapper;
import com.cyan.stargaze.dataset.infra.util.IdUtil;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 数据集文件仓储实现。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Repository
public class DatasetFileRepositoryImpl implements DatasetFileRepository {

    private final DatasetFileMapper datasetFileMapper;
    private final DatasetFileInfraConvert convert;

    public DatasetFileRepositoryImpl(DatasetFileMapper datasetFileMapper, DatasetFileInfraConvert convert) {
        this.datasetFileMapper = datasetFileMapper;
        this.convert = convert;
    }

    @Override
    public DatasetFile findById(String id) {
        DatasetFileDO datasetFileDO = datasetFileMapper.selectById(IdUtil.toLong(id));
        return datasetFileDO == null ? null : convert.toDatasetFile(datasetFileDO);
    }

    @Override
    public DatasetFile findByObjectKey(String objectKey) {
        LambdaQueryWrapper<DatasetFileDO> wrapper = new LambdaQueryWrapper<DatasetFileDO>()
                .eq(DatasetFileDO::getObjectKey, objectKey);
        DatasetFileDO datasetFileDO = datasetFileMapper.selectOne(wrapper);
        return datasetFileDO == null ? null : convert.toDatasetFile(datasetFileDO);
    }

    @Override
    public boolean existsByObjectKey(String objectKey) {
        LambdaQueryWrapper<DatasetFileDO> wrapper = new LambdaQueryWrapper<DatasetFileDO>()
                .eq(DatasetFileDO::getObjectKey, objectKey);
        return Optional.ofNullable(datasetFileMapper.selectCount(wrapper)).orElse(0L) > 0;
    }

    @Override
    public DatasetFile save(DatasetFile datasetFile) {
        DatasetFileDO datasetFileDO = convert.toDatasetFileDO(datasetFile);
        datasetFileMapper.insert(datasetFileDO);
        return findById(IdUtil.toString(datasetFileDO.getId()));
    }

    @Override
    public void deleteById(String id) {
        datasetFileMapper.deleteById(IdUtil.toLong(id));
    }
}
