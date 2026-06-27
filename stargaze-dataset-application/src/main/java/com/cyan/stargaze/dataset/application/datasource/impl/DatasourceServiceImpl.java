package com.cyan.stargaze.dataset.application.datasource.impl;

import com.cyan.arch.common.api.Assert;
import com.cyan.arch.common.api.SilentException;
import com.cyan.stargaze.dataset.application.datasource.DatasourceService;
import com.cyan.stargaze.dataset.application.datasource.bo.DatasourceBO;
import com.cyan.stargaze.dataset.application.datasource.cmd.DatasourceCmd;
import com.cyan.stargaze.dataset.application.datasource.convert.DatasourceAppConvert;
import com.cyan.stargaze.dataset.domain.datasource.DataSource;
import com.cyan.stargaze.dataset.domain.datasource.query.DataSourceListQuery;
import com.cyan.stargaze.dataset.domain.datasource.repository.DataSourceRepository;
import com.cyan.stargaze.dataset.domain.datasource.valobj.DatabaseValObj;
import com.cyan.stargaze.dataset.domain.datasource.valobj.TableMetaValObj;
import com.cyan.stargaze.dataset.domain.datasource.valobj.TableSampleValObj;
import com.cyan.stargaze.dataset.domain.datasource.valobj.TableSchemaValObj;
import com.cyan.stargaze.dataset.infra.connector.DataSourceConnector;
import com.cyan.stargaze.dataset.infra.connector.DataSourceConnectorFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 数据源应用服务实现。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DatasourceServiceImpl implements DatasourceService {

    private final DataSourceRepository dataSourceRepository;
    private final DataSourceConnectorFactory connectorFactory;
    private final DatasourceAppConvert convert;

    @Override
    @Transactional
    public DatasourceBO create(DatasourceCmd cmd) {
        DataSource dataSource = convert.toDataSource(cmd);
        dataSource = dataSource.save(dataSourceRepository);
        return convert.toDatasourceBO(dataSource);
    }

    @Override
    @Transactional
    public DatasourceBO update(DatasourceCmd cmd) {
        DataSource existing = dataSourceRepository.findById(cmd.getId());
        Assert.notNull(existing, new SilentException("数据源不存在"));
        DataSource dataSource = convert.toDataSource(cmd);
        // 名称唯一性由 domain.save 之外,update 复用 existing id
        dataSource.setId(existing.getId());
        dataSource = dataSource.update(dataSourceRepository);
        return convert.toDatasourceBO(dataSource);
    }

    @Override
    public List<DatasourceBO> list(DataSourceListQuery query) {
        return dataSourceRepository.list(query).stream()
                .map(convert::toDatasourceBO)
                .toList();
    }

    @Override
    public DatasourceBO findById(String id) {
        DataSource dataSource = dataSourceRepository.findById(id);
        Assert.notNull(dataSource, new SilentException("数据源不存在"));
        return convert.toDatasourceBO(dataSource);
    }

    @Override
    @Transactional
    public void delete(String id) {
        DataSource dataSource = dataSourceRepository.findById(id);
        Assert.notNull(dataSource, new SilentException("数据源不存在"));
        dataSource.delete(dataSourceRepository);
    }

    @Override
    public void testConnection(String id) {
        DataSource dataSource = dataSourceRepository.findById(id);
        Assert.notNull(dataSource, new SilentException("数据源不存在"));
        DataSourceConnector connector = connectorFactory.get(dataSource.getType());
        connector.testConnection(dataSource.getConfig());
        // 测试成功:标记正常
        dataSource.markActive();
        dataSourceRepository.update(dataSource);
    }

    @Override
    public void testConnection(DatasourceCmd cmd) {
        DataSource dataSource = convert.toDataSource(cmd);
        dataSource.validate();
        DataSourceConnector connector = connectorFactory.get(dataSource.getType());
        connector.testConnection(dataSource.getConfig());
    }

    @Override
    public boolean supportsSchema(String id) {
        DataSource dataSource = dataSourceRepository.findById(id);
        Assert.notNull(dataSource, new SilentException("数据源不存在"));
        return connectorFactory.get(dataSource.getType()).supportsSchema();
    }

    @Override
    public List<DatabaseValObj> listSchemas(String id) {
        DataSource dataSource = dataSourceRepository.findById(id);
        Assert.notNull(dataSource, new SilentException("数据源不存在"));
        DataSourceConnector connector = connectorFactory.get(dataSource.getType());
        return connector.listSchemas(dataSource.getConfig()).stream()
                .map(name -> new DatabaseValObj().setName(name))
                .toList();
    }

    @Override
    public List<String> listTables(String id, String schema) {
        DataSource dataSource = dataSourceRepository.findById(id);
        Assert.notNull(dataSource, new SilentException("数据源不存在"));
        DataSourceConnector connector = connectorFactory.get(dataSource.getType());
        return connector.listTables(dataSource.getConfig(), schema);
    }

    @Override
    public List<TableMetaValObj> listTablesRich(String id, String schema, String keyword) {
        DataSource dataSource = dataSourceRepository.findById(id);
        Assert.notNull(dataSource, new SilentException("数据源不存在"));
        DataSourceConnector connector = connectorFactory.get(dataSource.getType());
        return connector.listTablesRich(dataSource.getConfig(), schema, keyword);
    }

    @Override
    public TableSchemaValObj describeTable(String id, String schema, String tableName) {
        DataSource dataSource = dataSourceRepository.findById(id);
        Assert.notNull(dataSource, new SilentException("数据源不存在"));
        DataSourceConnector connector = connectorFactory.get(dataSource.getType());
        return connector.describeTable(dataSource.getConfig(), schema, tableName);
    }

    @Override
    public TableSampleValObj sampleTable(String id, String schema, String tableName, int limit) {
        DataSource dataSource = dataSourceRepository.findById(id);
        Assert.notNull(dataSource, new SilentException("数据源不存在"));
        DataSourceConnector connector = connectorFactory.get(dataSource.getType());
        return connector.sampleTable(dataSource.getConfig(), schema, tableName, limit);
    }
}
