package com.cyan.stargaze.dataset.application.dataset.impl;

import com.cyan.arch.common.api.Assert;
import com.cyan.arch.common.api.Page;
import com.cyan.arch.common.api.SilentException;
import com.cyan.stargaze.dataset.application.dataset.DatasetResolveService;
import com.cyan.stargaze.dataset.application.dataset.DatasetService;
import com.cyan.stargaze.dataset.application.dataset.ExcelDatasetService;
import com.cyan.stargaze.dataset.application.dataset.JoinDatasetService;
import com.cyan.stargaze.dataset.application.dataset.bo.DatasetBO;
import com.cyan.stargaze.dataset.application.dataset.bo.DatasetFieldBO;
import com.cyan.stargaze.dataset.application.dataset.bo.DatasetListBO;
import com.cyan.stargaze.dataset.application.dataset.bo.DatasetStatisticsBO;
import com.cyan.stargaze.dataset.application.dataset.bo.DatasetSyncBO;
import com.cyan.stargaze.dataset.application.dataset.bo.SqlPreviewBO;
import com.cyan.stargaze.dataset.application.dataset.cmd.DatasetCreateCmd;
import com.cyan.stargaze.dataset.application.dataset.cmd.DatasetFieldCmd;
import com.cyan.stargaze.dataset.application.dataset.cmd.DatasetSyncCmd;
import com.cyan.stargaze.dataset.application.dataset.cmd.DatasetUpdateCmd;
import com.cyan.stargaze.dataset.application.dataset.cmd.SqlPreviewCmd;
import com.cyan.stargaze.dataset.application.dataset.convert.DatasetAppConvert;
import com.cyan.stargaze.dataset.application.dataset.event.DatasetFieldChangedEvent;
import com.cyan.stargaze.dataset.application.dataset.support.DatasetBOAssembler;
import com.cyan.stargaze.dataset.application.datasource.SqlDatasetService;
import com.cyan.stargaze.dataset.domain.datasource.DataSource;
import com.cyan.stargaze.dataset.domain.datasource.repository.DataSourceRepository;
import com.cyan.stargaze.dataset.domain.datasource.valobj.ColumnValObj;
import com.cyan.stargaze.dataset.domain.datasource.valobj.TableSampleValObj;
import com.cyan.stargaze.dataset.domain.datasource.valobj.TableSchemaValObj;
import com.cyan.stargaze.dataset.domain.dataset.Dataset;
import com.cyan.stargaze.dataset.domain.dataset.DatasetField;
import com.cyan.stargaze.dataset.domain.dataset.config.DatasetConfig;
import com.cyan.stargaze.dataset.domain.dataset.config.DatasetConfigs;
import com.cyan.stargaze.dataset.domain.dataset.config.ExcelConfig;
import com.cyan.stargaze.dataset.domain.dataset.config.JoinConfig;
import com.cyan.stargaze.dataset.domain.dataset.config.SqlConfig;
import com.cyan.stargaze.dataset.domain.dataset.config.TableConfig;
import com.cyan.stargaze.dataset.domain.dataset.query.DatasetListQuery;
import com.cyan.stargaze.dataset.domain.dataset.repository.DatasetFieldRepository;
import com.cyan.stargaze.dataset.domain.dataset.repository.DatasetRepository;
import com.cyan.stargaze.dataset.enums.DataType;
import com.cyan.stargaze.dataset.enums.DatasetSourceType;
import com.cyan.stargaze.dataset.enums.DatasetSyncStatus;
import com.cyan.stargaze.dataset.enums.FieldType;
import com.cyan.stargaze.dataset.infra.connector.DataSourceConnector;
import com.cyan.stargaze.dataset.infra.connector.DataSourceConnectorFactory;
import com.cyan.stargaze.dataset.infra.connector.join.CompiledJoin;
import com.cyan.stargaze.dataset.infra.connector.join.JoinSqlCompiler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
    private final DatasetResolveService datasetResolveService;
    private final SqlDatasetService sqlDatasetService;
    private final JoinDatasetService joinDatasetService;
    private final ExcelDatasetService excelDatasetService;
    private final JoinSqlCompiler joinSqlCompiler;
    private final ApplicationEventPublisher eventPublisher;
    private final DatasetBOAssembler assembler;

    @Override
    @Transactional
    public DatasetBO create(DatasetCreateCmd cmd) {
        DatasetConfig config = extractConfig(cmd);
        validateSource(cmd.getSourceType(), cmd.getDatasourceId(), config);
        Dataset dataset = new Dataset()
                .setName(cmd.getName())
                .setDescription(cmd.getDescription())
                .setSourceType(cmd.getSourceType())
                .setDataSourceId(cmd.getDatasourceId())
                .setDefinition(DatasetConfigs.toJson(config))
                .setCreatedBy(cmd.getCreatedBy())
                .setUpdatedBy(cmd.getCreatedBy());
        // fields 为空时自动解析
        if (cmd.getFields() == null || cmd.getFields().isEmpty()) {
            List<DatasetFieldBO> resolved = datasetResolveService.resolveFields(cmd);
            dataset.setFields(toDomainFields(resolved));
        } else {
            dataset.setFields(convert.toDatasetFieldList(cmd.getFields()));
        }
        dataset = dataset.save(datasetRepository);
        return toDetailBO(dataset);
    }

    @Override
    @Transactional
    public DatasetBO update(String id, DatasetUpdateCmd cmd) {
        Dataset existing = datasetRepository.findById(id);
        Assert.notNull(existing, new SilentException("数据集不存在"));
        existing.setName(cmd.getName())
                .setDescription(cmd.getDescription())
                .setUpdatedBy(cmd.getUpdatedBy());
        if (cmd.getFields() != null) {
            existing.setFields(convert.toDatasetFieldList(cmd.getFields()));
        }
        Dataset updated = existing.update(datasetRepository);
        return toDetailBO(updated);
    }

    @Override
    public Page<DatasetListBO> page(DatasetListQuery query) {
        Page<Dataset> page = datasetRepository.page(query);
        List<DatasetListBO> list = assembler.assemble(page.getData());
        return new Page<>(list, page.getCurrent(), page.getSize(), page.getTotal());
    }

    @Override
    public DatasetBO findById(String id) {
        Dataset dataset = datasetRepository.findById(id);
        Assert.notNull(dataset, new SilentException("数据集不存在"));
        return toDetailBO(dataset);
    }

    @Override
    @Transactional
    public void delete(String id) {
        Dataset dataset = datasetRepository.findById(id);
        Assert.notNull(dataset, new SilentException("数据集不存在"));
        dataset.delete(datasetRepository);
    }

    @Override
    public SqlPreviewBO sqlPreview(SqlPreviewCmd cmd) {
        return sqlDatasetService.preview(cmd);
    }

    @Override
    @Transactional
    public DatasetSyncBO sync(String id, DatasetSyncCmd cmd) {
        // 一期占位:复用元数据刷新,返回 RUNNING(实际已同步完成)
        Dataset dataset = datasetRepository.findById(id);
        Assert.notNull(dataset, new SilentException("数据集不存在"));
        try {
            refresh(id);
        } catch (Exception e) {
            log.warn("数据集同步刷新失败, datasetId={}, err={}", id, e.getMessage());
        }
        OffsetDateTime now = OffsetDateTime.now();
        return new DatasetSyncBO()
                .setSyncId(id + "_" + now.toEpochSecond())
                .setDatasetId(id)
                .setStatus(DatasetSyncStatus.RUNNING)
                .setStartedAt(now)
                .setEstimatedCompleteAt(now);
    }

    @Override
    public DatasetSyncBO syncStatus(String id) {
        Dataset dataset = datasetRepository.findById(id);
        Assert.notNull(dataset, new SilentException("数据集不存在"));
        OffsetDateTime now = OffsetDateTime.now();
        return new DatasetSyncBO()
                .setSyncId(id + "_" + (dataset.getUpdatedAt() == null ? 0 : dataset.getUpdatedAt().toEpochSecond()))
                .setDatasetId(id)
                .setStatus(DatasetSyncStatus.SUCCESS)
                .setStartedAt(dataset.getUpdatedAt())
                .setCompletedAt(dataset.getUpdatedAt())
                .setDuration(0L)
                .setTotalRows(null)
                .setSyncedRows(null)
                .setErrorMessage(null);
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
        Assert.isTrue(datasetId.equals(field.getDatasetId()), new SilentException("字段不属于该数据集"));
        return convert.toDatasetFieldBO(field);
    }

    @Override
    public boolean exists(String datasetId) {
        return datasetRepository.existsById(datasetId);
    }

    @Override
    @Transactional
    public DatasetBO refresh(String id) {
        Dataset dataset = datasetRepository.findById(id);
        Assert.notNull(dataset, new SilentException("数据集不存在"));
        List<DatasetField> existing = datasetFieldRepository.listByDatasetId(id);
        Map<String, DataType> oldTypes = snapshotTypes(existing);
        List<DatasetField> fresh = resolveFreshFields(dataset);
        List<DatasetField> merged = dataset.rebuildFields(existing, fresh);
        datasetFieldRepository.deleteByDatasetId(id);
        datasetFieldRepository.saveAll(id, merged);
        publishFieldChangedEvent(dataset, oldTypes, merged);
        Dataset reloaded = datasetRepository.findById(id);
        return toDetailBO(reloaded);
    }

    @Override
    public TableSampleValObj preview(String id, int limit) {
        Dataset dataset = datasetRepository.findById(id);
        Assert.notNull(dataset, new SilentException("数据集不存在"));
        DatasetConfig config = DatasetConfigs.parse(dataset.getSourceType(), dataset.getDefinition());
        switch (dataset.getSourceType()) {
            case TABLE:
                return previewTable(dataset, (TableConfig) config, limit);
            case SQL:
                return sqlDatasetService.sample(dataset.getDataSourceId(),
                        ((SqlConfig) config).getSql(), ((SqlConfig) config).getSchema(), limit);
            case JOIN:
                return joinDatasetService.preview(dataset.getDataSourceId(), (JoinConfig) config, limit);
            case EXCEL:
                return excelDatasetService.preview((ExcelConfig) config, limit);
            default:
                throw new SilentException("暂不支持的数据集来源类型: " + dataset.getSourceType());
        }
    }

    // ==================== 私有 ====================

    private DatasetConfig extractConfig(DatasetCreateCmd cmd) {
        switch (cmd.getSourceType()) {
            case TABLE:
                Assert.notNull(cmd.getTableConfig(), new SilentException("缺少 table_config"));
                cmd.getTableConfig().validate();
                return cmd.getTableConfig();
            case SQL:
                Assert.notNull(cmd.getSqlConfig(), new SilentException("缺少 sql_config"));
                cmd.getSqlConfig().validate();
                return cmd.getSqlConfig();
            case JOIN:
                Assert.notNull(cmd.getJoinConfig(), new SilentException("缺少 join_config"));
                cmd.getJoinConfig().validate();
                return cmd.getJoinConfig();
            case EXCEL:
                Assert.notNull(cmd.getExcelConfig(), new SilentException("缺少 excel_config"));
                cmd.getExcelConfig().validate();
                return cmd.getExcelConfig();
            default:
                throw new SilentException("暂不支持的数据集来源类型: " + cmd.getSourceType());
        }
    }

    private void validateSource(DatasetSourceType sourceType, String datasourceId, DatasetConfig config) {
        switch (sourceType) {
            case TABLE, SQL, JOIN:
                Assert.notBlank(datasourceId, new SilentException("该来源类型必须指定数据源 ID"));
                break;
            case EXCEL:
                Assert.notBlank(((ExcelConfig) config).getFileId(), new SilentException("Excel 数据集必须指定文件 ID"));
                break;
            default:
                break;
        }
    }

    private List<DatasetField> resolveFreshFields(Dataset dataset) {
        DatasetConfig config = DatasetConfigs.parse(dataset.getSourceType(), dataset.getDefinition());
        List<DatasetFieldBO> bos;
        switch (dataset.getSourceType()) {
            case TABLE:
                TableSchemaValObj schema = describeSourceTable(dataset, (TableConfig) config);
                bos = toFieldBOs(schema.getColumns(), ((TableConfig) config).getTableName());
                break;
            case SQL:
                SqlConfig sqlConfig = (SqlConfig) config;
                bos = sqlDatasetService.resolveFields(dataset.getDataSourceId(), sqlConfig.getSql(), sqlConfig.getSchema());
                break;
            case JOIN:
                DataSource joinDs = loadDataSource(dataset);
                CompiledJoin compiled = joinSqlCompiler.compile((JoinConfig) config, joinDs.getConfig(), joinDs.getType());
                bos = toFieldBOs(compiled.getColumns(), null);
                break;
            case EXCEL:
                bos = excelDatasetService.resolveFields((ExcelConfig) config);
                break;
            default:
                throw new SilentException("暂不支持的数据集来源类型: " + dataset.getSourceType());
        }
        return toDomainFields(bos);
    }

    private TableSchemaValObj describeSourceTable(Dataset dataset, TableConfig config) {
        DataSource dataSource = loadDataSource(dataset);
        DataSourceConnector connector = connectorFactory.get(dataSource.getType());
        return connector.describeTable(dataSource.getConfig(), config.getSchema(), config.getTableName());
    }

    private TableSampleValObj previewTable(Dataset dataset, TableConfig config, int limit) {
        DataSource dataSource = loadDataSource(dataset);
        DataSourceConnector connector = connectorFactory.get(dataSource.getType());
        return connector.sampleTable(dataSource.getConfig(), config.getSchema(), config.getTableName(), limit);
    }

    private DataSource loadDataSource(Dataset dataset) {
        DataSource dataSource = dataSourceRepository.findById(dataset.getDataSourceId());
        Assert.notNull(dataSource, new SilentException("数据源不存在"));
        return dataSource;
    }

    private DatasetBO toDetailBO(Dataset dataset) {
        DatasetBO bo = convert.toDatasetBO(dataset);
        // config
        DatasetConfig config = DatasetConfigs.parse(dataset.getSourceType(), dataset.getDefinition());
        bo.setConfig(config);
        // fields
        bo.setFields(datasetFieldRepository.listByDatasetId(dataset.getId()).stream()
                .map(convert::toDatasetFieldBO).toList());
        // datasource name
        if (dataset.getDataSourceId() != null) {
            DataSource ds = dataSourceRepository.findById(dataset.getDataSourceId());
            if (ds != null) {
                bo.setDatasourceName(ds.getName());
            }
        }
        // statistics best-effort
        bo.setStatistics(buildStatistics(dataset, config));
        return bo;
    }

    private DatasetStatisticsBO buildStatistics(Dataset dataset, DatasetConfig config) {
        try {
            Long totalRows = countRows(dataset, config);
            return new DatasetStatisticsBO()
                    .setTotalRows(totalRows)
                    .setLastSyncRows(totalRows)
                    .setLastSyncAt(dataset.getUpdatedAt());
        } catch (Exception e) {
            return new DatasetStatisticsBO().setLastSyncAt(dataset.getUpdatedAt());
        }
    }

    private Long countRows(Dataset dataset, DatasetConfig config) {
        switch (dataset.getSourceType()) {
            case TABLE:
                TableConfig tc = (TableConfig) config;
                DataSource ds = loadDataSource(dataset);
                DataSourceConnector connector = connectorFactory.get(ds.getType());
                return connector.countTable(ds.getConfig(), tc.getSchema(), tc.getTableName());
            case SQL, JOIN:
                // SQL/JOIN 行数:包裹 select count(*) from (<sql>) (best-effort)
                return null;
            case EXCEL:
                return excelDatasetService.rowCount((ExcelConfig) config);
            default:
                return null;
        }
    }

    private List<DatasetFieldBO> toFieldBOs(List<ColumnValObj> columns, String sourceTable) {
        List<DatasetFieldBO> fields = new ArrayList<>();
        int ord = 1;
        for (ColumnValObj column : columns) {
            String displayName = column.getComment();
            if (displayName == null || displayName.isBlank()) {
                displayName = column.getName();
            }
            fields.add(new DatasetFieldBO()
                    .setFieldName(column.getName())
                    .setDisplayName(displayName)
                    .setDataType(column.getDataType())
                    .setFieldType(FieldType.DIMENSION)
                    .setSourceTable(sourceTable)
                    .setIsEnabled(true)
                    .setSortOrder(ord++));
        }
        return fields;
    }

    private List<DatasetField> toDomainFields(List<DatasetFieldBO> bos) {
        if (bos == null) {
            return null;
        }
        return bos.stream().map(bo -> new DatasetField()
                .setOriginName(bo.getFieldName())
                .setAlias(bo.getFieldName())
                .setDisplayName(bo.getDisplayName() == null ? bo.getFieldName() : bo.getDisplayName())
                .setFieldType(bo.getFieldType())
                .setDataType(bo.getDataType())
                .setSourceTable(bo.getSourceTable())
                .setIsEnabled(bo.getIsEnabled() == null || bo.getIsEnabled())
                .setOrd(bo.getSortOrder())).toList();
    }

    private void publishFieldChangedEvent(Dataset dataset, Map<String, DataType> oldTypes, List<DatasetField> merged) {
        Set<String> oldNames = oldTypes.keySet();
        Set<String> newNames = merged.stream().map(DatasetField::getOriginName).collect(Collectors.toSet());
        List<String> added = new ArrayList<>(newNames);
        added.removeAll(oldNames);
        List<String> removed = new ArrayList<>(oldNames);
        removed.removeAll(newNames);
        List<String> changed = new ArrayList<>();
        for (DatasetField f : merged) {
            DataType oldType = oldTypes.get(f.getOriginName());
            if (oldType != null && oldType != f.logicalDataType()) {
                changed.add(f.getOriginName());
            }
        }
        eventPublisher.publishEvent(new DatasetFieldChangedEvent(
                dataset.getId(), added, removed, changed, OffsetDateTime.now()));
    }

    private Map<String, DataType> snapshotTypes(List<DatasetField> existing) {
        if (existing == null || existing.isEmpty()) {
            return Map.of();
        }
        return existing.stream()
                .collect(Collectors.toMap(DatasetField::getOriginName, DatasetField::logicalDataType, (a, b) -> a));
    }
}
