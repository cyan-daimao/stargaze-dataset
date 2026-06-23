package com.cyan.stargaze.dataset.infra.connector;

import com.cyan.stargaze.dataset.domain.datasource.valobj.DataSourceConfig;
import com.cyan.stargaze.dataset.enums.DatasourceType;
import org.springframework.stereotype.Component;

/**
 * PostgreSQL 数据源适配器(亦为观星元数据库自身)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Component
public class PostgresConnector extends AbstractJdbcConnector {

    @Override
    public DatasourceType supportType() {
        return DatasourceType.POSTGRESQL;
    }

    @Override
    protected String driverClassName() {
        return "org.postgresql.Driver";
    }

    @Override
    protected String buildUrl(DataSourceConfig config) {
        return "jdbc:postgresql://" + config.getHost() + ":" + config.getPort()
                + "/" + (config.getDatabase() == null ? "" : config.getDatabase())
                + (config.getExtraParams() == null ? "" : "?" + config.getExtraParams());
    }
}
