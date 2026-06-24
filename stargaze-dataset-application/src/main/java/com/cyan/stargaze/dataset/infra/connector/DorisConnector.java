package com.cyan.stargaze.dataset.infra.connector;

import com.cyan.stargaze.dataset.domain.datasource.valobj.DataSourceConfig;
import com.cyan.stargaze.dataset.enums.DatasourceType;
import org.springframework.stereotype.Component;

/**
 * Apache Doris 数据源适配器(JDBC 兼容 MySQL 协议)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Component
public class DorisConnector extends AbstractJdbcConnector {

    @Override
    public DatasourceType supportType() {
        return DatasourceType.DORIS;
    }

    @Override
    public boolean supportsSchema() {
        return false;
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
}
