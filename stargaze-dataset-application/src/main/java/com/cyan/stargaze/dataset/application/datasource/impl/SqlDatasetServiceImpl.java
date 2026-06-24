package com.cyan.stargaze.dataset.application.datasource.impl;

import com.cyan.arch.common.api.Assert;
import com.cyan.arch.common.api.SilentException;
import com.cyan.stargaze.dataset.application.dataset.bo.DatasetFieldBO;
import com.cyan.stargaze.dataset.application.dataset.bo.SqlPreviewBO;
import com.cyan.stargaze.dataset.application.dataset.cmd.SqlPreviewCmd;
import com.cyan.stargaze.dataset.application.datasource.SqlDatasetService;
import com.cyan.stargaze.dataset.domain.datasource.DataSource;
import com.cyan.stargaze.dataset.domain.datasource.repository.DataSourceRepository;
import com.cyan.stargaze.dataset.domain.datasource.valobj.ColumnValObj;
import com.cyan.stargaze.dataset.domain.datasource.valobj.TableSampleValObj;
import com.cyan.stargaze.dataset.domain.datasource.valobj.TableSchemaValObj;
import com.cyan.stargaze.dataset.enums.FieldType;
import com.cyan.stargaze.dataset.infra.connector.DataSourceConnector;
import com.cyan.stargaze.dataset.infra.connector.DataSourceConnectorFactory;
import com.cyan.stargaze.dataset.infra.connector.sql.SqlSafetyResult;
import com.cyan.stargaze.dataset.infra.connector.sql.SqlSafetyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * SQL 数据集应用服务实现。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class SqlDatasetServiceImpl implements SqlDatasetService {

    private final DataSourceRepository dataSourceRepository;
    private final DataSourceConnectorFactory connectorFactory;
    private final SqlSafetyValidator sqlSafetyValidator;

    @Override
    public SqlPreviewBO preview(SqlPreviewCmd cmd) {
        DataSource dataSource = loadDataSource(cmd.getDatasourceId());
        String sql = sanitize(cmd.getSql());
        DataSourceConnector connector = connectorFactory.get(dataSource.getType());
        int limit = cmd.getLimit() == null || cmd.getLimit() <= 0 ? 100 : cmd.getLimit();

        long start = System.nanoTime();
        // 列 + 类型
        TableSchemaValObj schema = connector.describeSql(dataSource.getConfig(), null, sql);
        // 采样行
        TableSampleValObj sample = connector.sampleSql(dataSource.getConfig(), null, sql, limit);
        double elapsed = (System.nanoTime() - start) / 1_000_000_000.0;

        return new SqlPreviewBO()
                .setColumns(schema.getColumns())
                .setRows(sample.getRows())
                .setTotalRows((long) sample.getRows().size())
                .setExecutionTime(elapsed)
                .setIsTruncated(sample.getRows().size() >= limit);
    }

    @Override
    public TableSchemaValObj schema(String datasourceId, String sql, String schema) {
        DataSource dataSource = loadDataSource(datasourceId);
        String safe = sanitize(sql);
        DataSourceConnector connector = connectorFactory.get(dataSource.getType());
        return connector.describeSql(dataSource.getConfig(), schema, safe);
    }

    @Override
    public TableSampleValObj sample(String datasourceId, String sql, String schema, int limit) {
        DataSource dataSource = loadDataSource(datasourceId);
        String safe = sanitize(sql);
        DataSourceConnector connector = connectorFactory.get(dataSource.getType());
        return connector.sampleSql(dataSource.getConfig(), schema, safe, limit);
    }

    @Override
    public List<DatasetFieldBO> resolveFields(String datasourceId, String sql, String schema) {
        TableSchemaValObj schemaValObj = schema(datasourceId, sql, schema);
        List<DatasetFieldBO> fields = new ArrayList<>();
        int ord = 1;
        for (ColumnValObj column : schemaValObj.getColumns()) {
            fields.add(new DatasetFieldBO()
                    .setFieldName(column.getName())
                    .setDisplayName(column.getName())
                    .setDataType(column.getDataType())
                    .setFieldType(FieldType.DIMENSION)
                    .setIsEnabled(true)
                    .setSortOrder(ord++));
        }
        return fields;
    }

    private DataSource loadDataSource(String datasourceId) {
        DataSource dataSource = dataSourceRepository.findById(datasourceId);
        Assert.notNull(dataSource, new SilentException("数据源不存在"));
        return dataSource;
    }

    private String sanitize(String sql) {
        SqlSafetyResult result = sqlSafetyValidator.validate(sql);
        Assert.isTrue(result.isValid(), new SilentException(result.getMessage()));
        return result.getSanitized();
    }
}
