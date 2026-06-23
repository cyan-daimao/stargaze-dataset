package com.cyan.stargaze.dataset.application.dataset.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.cyan.arch.common.api.Assert;
import com.cyan.arch.common.api.SilentException;
import com.cyan.stargaze.dataset.application.dataset.DatasetService;
import com.cyan.stargaze.dataset.application.dataset.bo.DatasetBO;
import com.cyan.stargaze.dataset.application.dataset.bo.DatasetFieldBO;
import com.cyan.stargaze.dataset.application.dataset.cmd.DatasetCmd;
import com.cyan.stargaze.dataset.application.dataset.convert.DatasetAppConvert;
import com.cyan.stargaze.dataset.domain.datasource.DataSource;
import com.cyan.stargaze.dataset.domain.datasource.repository.DataSourceRepository;
import com.cyan.stargaze.dataset.domain.datasource.valobj.ColumnValObj;
import com.cyan.stargaze.dataset.domain.datasource.valobj.TableSampleValObj;
import com.cyan.stargaze.dataset.domain.datasource.valobj.TableSchemaValObj;
import com.cyan.stargaze.dataset.domain.dataset.Dataset;
import com.cyan.stargaze.dataset.domain.dataset.DatasetField;
import com.cyan.stargaze.dataset.domain.dataset.query.DatasetListQuery;
import com.cyan.stargaze.dataset.domain.dataset.repository.DatasetFieldRepository;
import com.cyan.stargaze.dataset.domain.dataset.repository.DatasetRepository;
import com.cyan.stargaze.dataset.enums.DataType;
import com.cyan.stargaze.dataset.enums.DatasetSourceType;
import com.cyan.stargaze.dataset.enums.FieldType;
import com.cyan.stargaze.dataset.infra.connector.DataSourceConnector;
import com.cyan.stargaze.dataset.infra.connector.DataSourceConnectorFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 数据集应用服务实现。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DatasetServiceImpl implements DatasetService {

    private final DatasetRepository datasetRepository;
    private final DatasetFieldRepository datasetFieldRepository;
    private final DataSourceRepository dataSourceRepository;
    private final DataSourceConnectorFactory connectorFactory;
    private final DatasetAppConvert convert;

    @Override
    @Transactional
    public DatasetBO create(DatasetCmd cmd) {
        Dataset dataset = convert.toDataset(cmd);
        dataset = dataset.save(datasetRepository);
        return convert.toDatasetBO(dataset);
    }

    @Override
    @Transactional
    public DatasetBO update(DatasetCmd cmd) {
        Dataset existing = datasetRepository.findById(cmd.getId());
        Assert.notNull(existing, new SilentException("数据集不存在"));
        Dataset dataset = convert.toDataset(cmd);
        dataset.setId(existing.getId());
        dataset.setVersion(existing.getVersion());
        dataset = dataset.update(datasetRepository);
        return convert.toDatasetBO(dataset);
    }

    @Override
    public List<DatasetBO> list(DatasetListQuery query) {
        return datasetRepository.list(query).stream()
                .map(convert::toDatasetBO)
                .toList();
    }

    @Override
    public DatasetBO findById(String id) {
        Dataset dataset = datasetRepository.findById(id);
        Assert.notNull(dataset, new SilentException("数据集不存在"));
        return convert.toDatasetBO(dataset);
    }

    @Override
    @Transactional
    public void delete(String id) {
        Dataset dataset = datasetRepository.findById(id);
        Assert.notNull(dataset, new SilentException("数据集不存在"));
        dataset.delete(datasetRepository);
    }

    @Override
    @Transactional
    public DatasetBO refresh(String id) {
        Dataset dataset = datasetRepository.findById(id);
        Assert.notNull(dataset, new SilentException("数据集不存在"));
        // 仅单表/SQL 类型支持自动刷新结构
        if (dataset.getSourceType() != DatasetSourceType.TABLE) {
            return convert.toDatasetBO(dataset);
        }
        TableSchemaValObj schema = describeSourceTable(id);
        // 将源表字段同步为数据集字段(默认维度,数据类型推断)
        List<DatasetField> refreshedFields = schema.getColumns().stream()
                .map(this::toDatasetField)
                .toList();
        // 全量重建字段
        datasetFieldRepository.deleteByDatasetId(id);
        datasetFieldRepository.saveAll(id, refreshedFields);
        // 重新加载
        Dataset reloaded = datasetRepository.findById(id);
        // 发布字段变更事件(供 metric 订阅做口径校验)——一期日志占位
        log.info("数据集字段变更通知 dataset.field.changed, datasetId={}, fieldCount={}",
                id, refreshedFields.size());
        return convert.toDatasetBO(reloaded);
    }

    @Override
    public TableSampleValObj preview(String id, int limit) {
        Dataset dataset = datasetRepository.findById(id);
        Assert.notNull(dataset, new SilentException("数据集不存在"));
        DataSource dataSource = dataSourceRepository.findById(dataset.getDataSourceId());
        Assert.notNull(dataSource, new SilentException("数据源不存在"));
        JSONObject def = JSON.parseObject(dataset.getDefinition());
        String schema = def.getString("schema");
        String table = def.getString("table");
        Assert.notBlank(table, new SilentException("数据集来源定义缺少表名"));
        DataSourceConnector connector = connectorFactory.get(dataSource.getType());
        return connector.sampleTable(dataSource.getConfig(), schema, table, limit);
    }

    @Override
    public List<DatasetFieldBO> listFields(String datasetId) {
        return datasetFieldRepository.listByDatasetId(datasetId).stream()
                .map(convert::toDatasetFieldBO)
                .toList();
    }

    @Override
    public DatasetFieldBO resolveField(String datasetId, String fieldId) {
        DatasetField field = datasetFieldRepository.findById(fieldId);
        Assert.notNull(field, new SilentException("字段不存在"));
        Assert.isTrue(datasetId.equals(field.getDatasetId()),
                new SilentException("字段不属于该数据集"));
        return convert.toDatasetFieldBO(field);
    }

    @Override
    public boolean exists(String datasetId) {
        return datasetRepository.existsById(datasetId);
    }

    @Override
    public TableSchemaValObj describeSourceTable(String datasetId) {
        Dataset dataset = datasetRepository.findById(datasetId);
        Assert.notNull(dataset, new SilentException("数据集不存在"));
        DataSource dataSource = dataSourceRepository.findById(dataset.getDataSourceId());
        Assert.notNull(dataSource, new SilentException("数据源不存在"));
        JSONObject def = JSON.parseObject(dataset.getDefinition());
        String schema = def.getString("schema");
        String table = def.getString("table");
        Assert.notBlank(table, new SilentException("数据集来源定义缺少表名"));
        DataSourceConnector connector = connectorFactory.get(dataSource.getType());
        return connector.describeTable(dataSource.getConfig(), schema, table);
    }

    /**
     * 源表列 -> 数据集字段(默认维度,类型推断)
     */
    private DatasetField toDatasetField(ColumnValObj column) {
        return new DatasetField()
                .setOriginName(column.getName())
                .setAlias(column.getName())
                .setFieldType(FieldType.DIMENSION)
                .setDataType(inferDataType(column.getDataType()))
                .setHidden(false)
                .setOrd(0);
    }

    /**
     * 源库类型 -> 统一逻辑类型推断
     */
    private DataType inferDataType(String sourceType) {
        if (sourceType == null) {
            return DataType.STRING;
        }
        String lower = sourceType.toLowerCase();
        if (lower.contains("int") || lower.contains("bigint") || lower.contains("smallint") || lower.contains("tinyint")) {
            return DataType.INT;
        }
        if (lower.contains("decimal") || lower.contains("numeric") || lower.contains("double") || lower.contains("float")) {
            return DataType.DECIMAL;
        }
        if (lower.contains("bool")) {
            return DataType.BOOLEAN;
        }
        if (lower.contains("datetime") || lower.contains("timestamp")) {
            return DataType.DATETIME;
        }
        if (lower.contains("date")) {
            return DataType.DATE;
        }
        return DataType.STRING;
    }
}
