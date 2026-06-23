package com.cyan.stargaze.dataset.infra.connector;

import com.cyan.stargaze.dataset.domain.datasource.valobj.DataSourceConfig;
import com.cyan.stargaze.dataset.enums.DatasourceType;
import org.springframework.stereotype.Component;

/**
 * MySQL 数据源适配器。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Component
public class MysqlConnector extends AbstractJdbcConnector {

    @Override
    public DatasourceType supportType() {
        return DatasourceType.MYSQL;
    }

    @Override
    protected String driverClassName() {
        return "com.mysql.cj.jdbc.Driver";
    }

    @Override
    protected String buildUrl(DataSourceConfig config) {
        return "jdbc:mysql://" + config.getHost() + ":" + config.getPort()
                + "/" + (config.getDatabase() == null ? "" : config.getDatabase())
                + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
                + (config.getExtraParams() == null ? "" : "&" + config.getExtraParams());
    }

    @Override
    protected char identifierQuote() {
        return '`';
    }

    @Override
    protected boolean isSystemSchema(String name) {
        return super.isSystemSchema(name) || name.equalsIgnoreCase("sys");
    }
}
