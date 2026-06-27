package com.cyan.stargaze.dataset.application.dataset.support;

import com.cyan.stargaze.dataset.application.dataset.bo.DatasetListBO;
import com.cyan.stargaze.dataset.domain.dataset.Dataset;
import com.cyan.stargaze.dataset.domain.dataset.repository.DatasetFieldRepository;
import com.cyan.stargaze.dataset.domain.dataset.valobj.FieldCountStat;
import com.cyan.stargaze.dataset.domain.datasource.DataSource;
import com.cyan.stargaze.dataset.domain.datasource.repository.DataSourceRepository;
import com.cyan.stargaze.dataset.enums.DatasetSourceType;
import com.cyan.stargaze.dataset.enums.FieldType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 数据集列表项批量组装器(避免 N+1):一次查询填充 datasource_name / 字段统计 / creator 名。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class DatasetBOAssembler {

    private final DataSourceRepository dataSourceRepository;
    private final DatasetFieldRepository datasetFieldRepository;
    private final EmployeeNameResolver employeeNameResolver;

    /**
     * 批量组装列表项 BO。
     */
    public List<DatasetListBO> assemble(List<Dataset> datasets) {
        if (datasets == null || datasets.isEmpty()) {
            return List.of();
        }
        List<String> datasetIds = datasets.stream().map(Dataset::getId).toList();
        List<String> datasourceIds = datasets.stream().map(Dataset::getDataSourceId)
                .filter(Objects::nonNull).distinct().toList();
        List<String> creatorIds = datasets.stream().map(Dataset::getCreatedBy)
                .filter(Objects::nonNull).distinct().toList();

        // 字段统计: datasetId -> stat
        Map<String, FieldCountStat> statMap = datasetFieldRepository.countByDatasetIds(datasetIds).stream()
                .collect(Collectors.toMap(FieldCountStat::getDatasetId, s -> s, (a, b) -> a));
        // 数据源名: datasourceId -> name
        Map<String, String> dsNameMap = new HashMap<>();
        if (!datasourceIds.isEmpty()) {
            for (DataSource ds : dataSourceRepository.findByIds(datasourceIds)) {
                dsNameMap.put(ds.getId(), ds.getName());
            }
        }
        // 创建人名: creatorId -> cnName
        Map<String, String> creatorNameMap = employeeNameResolver.resolveNames(creatorIds);

        List<DatasetListBO> result = new ArrayList<>(datasets.size());
        for (Dataset d : datasets) {
            FieldCountStat stat = statMap.get(d.getId());
            result.add(new DatasetListBO()
                    .setId(d.getId())
                    .setName(d.getName())
                    .setDisplayName(d.getDisplayName())
                    .setDescription(d.getDescription())
                    .setSourceType(d.getSourceType())
                    .setSourceTypeName(d.getSourceType() == null ? null : d.getSourceType().getDisplayName())
                    .setDatasourceId(d.getDataSourceId())
                    .setDatasourceName(d.getDataSourceId() == null ? null : dsNameMap.get(d.getDataSourceId()))
                    .setStatus(d.getStatus())
                    .setFieldCount(stat == null ? 0 : stat.getTotal())
                    .setDimensionCount(stat == null ? 0 : stat.getDimension())
                    .setMeasureCount(stat == null ? 0 : stat.getMeasure())
                    .setVersion(d.getVersion())
                    .setCreatedBy(d.getCreatedBy())
                    .setCreator(d.getCreatedBy() == null ? null : creatorNameMap.get(d.getCreatedBy()))
                    .setCreatedAt(d.getCreatedAt())
                    .setUpdatedAt(d.getUpdatedAt())
                    .setLastSyncAt(d.getUpdatedAt()));
        }
        return result;
    }
}
