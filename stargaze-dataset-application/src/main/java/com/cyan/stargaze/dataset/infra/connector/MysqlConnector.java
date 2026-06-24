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

    /**
     * 富列表表(MySQL/StarRocks/Doris): 注释/行数来自 information_schema.tables,字段数聚合。
     */
    @Override
    protected String listTablesRichSql() {
        return "SELECT t.table_name AS table_name, "
                + "IFNULL(t.table_comment, '') AS table_comment, "
                + "(SELECT count(*) FROM information_schema.columns ic WHERE ic.table_schema = t.table_schema AND ic.table_name = t.table_name) AS field_count, "
                + "t.table_rows AS row_count "
                + "FROM information_schema.tables t WHERE t.table_schema = ? "
                + "ORDER BY t.table_name";
    }

    /**
     * 富字段(MySQL): 注释来自 COLUMN_COMMENT,主键来自 information_schema.statistics。
     */
    @Override
    protected String describeTableSql() {
        return "SELECT c.column_name, c.data_type, c.is_nullable, c.ordinal_position, "
                + "c.column_comment AS column_comment, "
                + "EXISTS(SELECT 1 FROM information_schema.statistics s "
                + "WHERE s.table_schema = c.table_schema AND s.table_name = c.table_name "
                + "AND s.column_name = c.column_name AND s.index_name = 'PRIMARY') AS is_primary_key "
                + "FROM information_schema.columns c WHERE c.table_schema = ? AND c.table_name = ? "
                + "ORDER BY c.ordinal_position";
    }
}
