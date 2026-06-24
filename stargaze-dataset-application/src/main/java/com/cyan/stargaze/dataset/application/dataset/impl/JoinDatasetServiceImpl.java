package com.cyan.stargaze.dataset.application.dataset.impl;

import com.cyan.arch.common.api.Assert;
import com.cyan.arch.common.api.SilentException;
import com.cyan.stargaze.dataset.application.dataset.JoinDatasetService;
import com.cyan.stargaze.dataset.application.dataset.bo.DatasetFieldBO;
import com.cyan.stargaze.dataset.domain.datasource.DataSource;
import com.cyan.stargaze.dataset.domain.datasource.repository.DataSourceRepository;
import com.cyan.stargaze.dataset.domain.datasource.valobj.ColumnValObj;
import com.cyan.stargaze.dataset.domain.datasource.valobj.TableSampleValObj;
import com.cyan.stargaze.dataset.domain.dataset.config.JoinConfig;
import com.cyan.stargaze.dataset.enums.FieldType;
import com.cyan.stargaze.dataset.infra.connector.DataSourceConnector;
import com.cyan.stargaze.dataset.infra.connector.DataSourceConnectorFactory;
import com.cyan.stargaze.dataset.infra.connector.join.CompiledJoin;
import com.cyan.stargaze.dataset.infra.connector.join.JoinSqlCompiler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * JOIN 数据集应用服务实现。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class JoinDatasetServiceImpl implements JoinDatasetService {

    private final DataSourceRepository dataSourceRepository;
    private final DataSourceConnectorFactory connectorFactory;
    private final JoinSqlCompiler joinSqlCompiler;

    @Override
    public List<DatasetFieldBO> resolveFields(String datasourceId, JoinConfig config) {
        DataSource dataSource = loadDataSource(datasourceId);
        CompiledJoin compiled = joinSqlCompiler.compile(config, dataSource.getConfig(), dataSource.getType());
        List<DatasetFieldBO> fields = new ArrayList<>();
        int ord = 1;
        for (ColumnValObj column : compiled.getColumns()) {
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

    @Override
    public TableSampleValObj preview(String datasourceId, JoinConfig config, int limit) {
        DataSource dataSource = loadDataSource(datasourceId);
        CompiledJoin compiled = joinSqlCompiler.compile(config, dataSource.getConfig(), dataSource.getType());
        DataSourceConnector connector = connectorFactory.get(dataSource.getType());
        return connector.sampleSql(dataSource.getConfig(), config.getMainTable().getSchema(), compiled.getSql(), limit);
    }

    private DataSource loadDataSource(String datasourceId) {
        DataSource dataSource = dataSourceRepository.findById(datasourceId);
        Assert.notNull(dataSource, new SilentException("数据源不存在"));
        return dataSource;
    }
}
