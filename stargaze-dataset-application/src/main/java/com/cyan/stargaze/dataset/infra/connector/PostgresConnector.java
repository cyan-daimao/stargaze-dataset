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

    /**
     * 富列表表(PG): 注释来自 pg_description,行数用 reltuples 估算,字段数用 information_schema 聚合。
     */
    @Override
    protected String listTablesRichSql() {
        return "SELECT c.relname AS table_name, "
                + "COALESCE(obj_description(c.oid), '') AS table_comment, "
                + "(SELECT count(*) FROM information_schema.columns ic WHERE ic.table_schema = n.nspname AND ic.table_name = c.relname) AS field_count, "
                + "c.reltuples::bigint AS row_count "
                + "FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace "
                + "WHERE n.nspname = ? AND c.relkind = 'r' "
                + "ORDER BY c.relname";
    }

    /**
     * 富字段(PG): 注释来自 col_description,主键来自 pg_constraint。
     */
    @Override
    protected String describeTableSql() {
        return "SELECT a.attname AS column_name, format_type(a.atttypid, a.atttypmod) AS data_type, "
                + "a.attnotnull = false AS is_nullable, a.attnum AS ordinal_position, "
                + "col_description(a.attrelid, a.attnum) AS column_comment, "
                + "EXISTS(SELECT 1 FROM pg_constraint con WHERE con.conrelid = a.attrelid "
                + "AND con.contype = 'p' AND a.attnum = ANY(con.conkey)) AS is_primary_key "
                + "FROM pg_attribute a WHERE a.attrelid = (? || '.' || ?)::regclass AND a.attnum > 0 AND NOT a.attisdropped "
                + "ORDER BY a.attnum";
    }

    /**
     * 行数估算用 reltuples,避免全表 count。
     */
    @Override
    protected String countTableSql(String schema, String tableName) {
        return "SELECT c.reltuples::bigint FROM pg_class c "
                + "JOIN pg_namespace n ON n.oid = c.relnamespace "
                + "WHERE n.nspname = '" + schema.replace("'", "''") + "' "
                + "AND c.relname = '" + tableName.replace("'", "''") + "'";
    }
}
