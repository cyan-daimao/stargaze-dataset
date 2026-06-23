package com.cyan.stargaze.dataset.infra.connector;

import com.cyan.stargaze.dataset.domain.datasource.valobj.DataSourceConfig;
import com.cyan.stargaze.dataset.enums.DatasourceType;
import org.springframework.stereotype.Component;

/**
 * ClickHouse 数据源适配器。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Component
public class ClickhouseConnector extends AbstractJdbcConnector {

    @Override
    public DatasourceType supportType() {
        return DatasourceType.CLICKHOUSE;
    }

    @Override
    protected String driverClassName() {
        return "com.clickhouse.jdbc.ClickHouseDriver";
    }

    @Override
    protected String buildUrl(DataSourceConfig config) {
        return "jdbc:ch://" + config.getHost() + ":" + config.getPort()
                + "/" + (config.getDatabase() == null ? "" : config.getDatabase())
                + (config.getExtraParams() == null ? "" : "?" + config.getExtraParams());
    }

    @Override
    protected boolean isSystemSchema(String name) {
        return super.isSystemSchema(name) || name.equalsIgnoreCase("system")
                || name.equalsIgnoreCase("INFORMATION_SCHEMA")
                || name.equalsIgnoreCase("default");
    }
}
