package com.cyan.stargaze.dataset.infra.starrocks;

import com.cyan.arch.common.api.SilentException;
import com.cyan.stargaze.dataset.domain.dataset.DatasetField;
import com.cyan.stargaze.dataset.domain.datasource.DataSource;
import com.cyan.stargaze.dataset.domain.datasource.valobj.DataSourceConfig;
import com.cyan.stargaze.dataset.enums.DataType;
import com.cyan.stargaze.dataset.enums.DatasourceType;
import com.cyan.stargaze.dataset.infra.config.DatasetStarRocksProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * StarRocks 表与 catalog 管理组件。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StarRocksTableManager {

    private static final String MYSQL_DRIVER_URL = "file:///opt/starrocks/jdbc-drivers/mariadb-java-client-3.3.2.jar";
    private static final String MYSQL_DRIVER_CLASS = "org.mariadb.jdbc.Driver";
    private static final String POSTGRESQL_DRIVER_URL = "file:///opt/starrocks/jdbc-drivers/postgresql-42.4.4.jar";
    private static final String POSTGRESQL_DRIVER_CLASS = "org.postgresql.Driver";
    private static final String CLICKHOUSE_DRIVER_URL = "file:///opt/starrocks/jdbc-drivers/clickhouse-jdbc-0.4.6.jar";
    private static final String CLICKHOUSE_DRIVER_CLASS = "com.clickhouse.jdbc.ClickHouseDriver";

    private final DatasetStarRocksProperties properties;

    /**
     * 创建库。
     */
    public void ensureDatabase(String database) {
        execute("CREATE DATABASE IF NOT EXISTS " + quote(database));
    }

    /**
     * 重建表。
     */
    public void recreateTable(String database, String table, List<DatasetField> fields) {
        ensureDatabase(database);
        execute("DROP TABLE IF EXISTS " + quote(database, table));
        execute(buildCreateTableSql(database, table, fields));
    }

    /**
     * 清空表。
     */
    public void truncateTable(String database, String table) {
        execute("TRUNCATE TABLE " + quote(database, table));
    }

    /**
     * 批量写入。
     */
    public long insertRows(String database, String table, List<DatasetField> fields, List<Map<String, Object>> rows) {
        if (rows == null || rows.isEmpty()) {
            return 0L;
        }
        List<String> columns = fields.stream().map(DatasetField::getOriginName).toList();
        String placeholders = String.join(", ", columns.stream().map(c -> "?").toList());
        String sql = "INSERT INTO " + quote(database, table) + " (" + quoteList(columns) + ") VALUES (" + placeholders + ")";
        try (Connection connection = connection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            int batchSize = Math.max(1, properties.getInsertBatchSize());
            long inserted = 0L;
            for (Map<String, Object> row : rows) {
                for (int i = 0; i < columns.size(); i++) {
                    statement.setObject(i + 1, normalizeValue(row.get(columns.get(i))));
                }
                statement.addBatch();
                inserted++;
                if (inserted % batchSize == 0) {
                    statement.executeBatch();
                }
            }
            statement.executeBatch();
            return inserted;
        } catch (Exception e) {
            throw new SilentException("写入 StarRocks 表失败: " + e.getMessage());
        }
    }

    /**
     * 确保 external catalog 存在。
     */
    public void ensureExternalCatalog(String catalogName, DataSource dataSource) {
        ensureExternalCatalog(catalogName, dataSource, false);
    }

    /**
     * 重建 external catalog。
     */
    public void recreateExternalCatalog(String catalogName, DataSource dataSource) {
        ensureExternalCatalog(catalogName, dataSource, true);
    }

    private void ensureExternalCatalog(String catalogName, DataSource dataSource, boolean forceRecreate) {
        DataSourceConfig config = dataSource.getConfig();
        String rawJdbcUrl = config.getJdbcUrl();
        String jdbcUri = jdbcUri(dataSource.getType(), config);
        String driverUrl = driverUrl(dataSource.getType());
        if (driverUrl == null || driverUrl.isBlank()) {
            throw new SilentException("StarRocks external catalog 创建失败:缺少 " + driverUrlConfigKey(dataSource.getType()));
        }
        String driverClass = driverClass(dataSource.getType());
        // 当 JDBC URL 被标准化后与原值不同时，必须强制重建 catalog，
        // 否则 StarRocks BE 会继续使用 catalog 中已缓存的旧 URL
        boolean urlNormalized = rawJdbcUrl != null && !rawJdbcUrl.isBlank()
                && !rawJdbcUrl.trim().equals(jdbcUri);
        if (urlNormalized) {
            log.info("JDBC URL 标准化变更，强制重建 catalog={}, 原值={}, 新值={}", catalogName, rawJdbcUrl, jdbcUri);
            forceRecreate = true;
        }
        log.info("创建/校验 StarRocks external catalog={}, jdbcUri={}, driverUrl={}, driverClass={}, forceRecreate={}",
                catalogName, jdbcUri, driverUrl, driverClass, forceRecreate);
        String sql = "CREATE EXTERNAL CATALOG IF NOT EXISTS " + quote(catalogName)
                + " PROPERTIES ("
                + prop("type", "jdbc") + ", "
                + prop("user", config.getUsername()) + ", "
                + prop("password", config.getPassword()) + ", "
                + prop("jdbc_uri", jdbcUri) + ", "
                + prop("driver_url", driverUrl) + ", "
                + prop("driver_class", driverClass)
                + ")";
        if (forceRecreate || catalogNeedsRebuild(catalogName, config.getUsername(), jdbcUri, driverUrl, driverClass)) {
            execute("DROP CATALOG IF EXISTS " + quote(catalogName));
        }
        execute(sql);
    }

    private String buildCreateTableSql(String database, String table, List<DatasetField> fields) {
        if (fields == null || fields.isEmpty()) {
            throw new SilentException("StarRocks 建表失败:数据集字段为空");
        }
        List<String> columns = new ArrayList<>();
        for (DatasetField field : fields) {
            columns.add(quote(field.getOriginName()) + " " + toStarRocksType(field.logicalDataType()));
        }
        return "CREATE TABLE IF NOT EXISTS " + quote(database, table)
                + " (" + String.join(", ", columns) + ") "
                + "ENGINE=OLAP "
                + "DISTRIBUTED BY RANDOM BUCKETS 10 "
                + "PROPERTIES (\"replication_num\" = \"1\")";
    }

    private String toStarRocksType(DataType type) {
        if (type == null) {
            return "VARCHAR(65533)";
        }
        return switch (type) {
            case INT -> "BIGINT";
            case DECIMAL -> "DECIMAL(18,4)";
            case DATE -> "DATE";
            case DATETIME -> "DATETIME";
            case BOOLEAN -> "BOOLEAN";
            case STRING -> "VARCHAR(65533)";
        };
    }

    private Object normalizeValue(Object value) {
        if (value instanceof java.util.Date date) {
            return new Timestamp(date.toInstant().toEpochMilli());
        }
        if (value instanceof LocalDateTime dateTime) {
            return Timestamp.valueOf(dateTime);
        }
        if (value instanceof LocalDate date) {
            return java.sql.Date.valueOf(date);
        }
        if (value instanceof java.time.OffsetDateTime dateTime) {
            return Timestamp.from(dateTime.toInstant());
        }
        return value;
    }

    private String jdbcUri(DatasourceType type, DataSourceConfig config) {
        if (config.getJdbcUrl() != null && !config.getJdbcUrl().isBlank()) {
            return normalizeJdbcUrl(config.getJdbcUrl());
        }
        String database = config.getDatabase() == null ? "" : "/" + config.getDatabase();
        return switch (type) {
            case POSTGRESQL -> "jdbc:postgresql://" + config.getHost() + ":" + config.getPort() + database;
            case MYSQL, STARROCKS, DORIS -> "jdbc:mysql://" + config.getHost() + ":" + config.getPort() + database;
            case CLICKHOUSE -> "jdbc:clickhouse://" + config.getHost() + ":" + config.getPort() + database;
            default -> throw new SilentException("暂不支持创建该数据源 catalog: " + type);
        };
    }

    /**
     * 标准化 JDBC URL，去除重复的数据库路径段。
     * <p>
     * MariaDB Connector/J 3.x 严格校验 URL 格式，
     * 不接受 {@code jdbc:mysql://host:port/db/db} 这种双路径格式，
     * 而有些数据源配置中可能存储了带重复段的 URL。
     * </p>
     *
     * @param jdbcUrl 原始 JDBC URL
     * @return 标准化后的 URL（只保留第一个路径段作为数据库名）
     */
    private String normalizeJdbcUrl(String jdbcUrl) {
        if (jdbcUrl == null || jdbcUrl.isBlank()) {
            return jdbcUrl;
        }
        // jdbc:mysql://10.0.0.2:3306/cyan_dataman/cyan_dataman?useSSL=false
        // → jdbc:mysql://10.0.0.2:3306/cyan_dataman?useSSL=false
        String normalized = jdbcUrl.trim();
        normalized = normalized.replaceAll(
                "^(jdbc:[a-z]+://[^/]+/[^/?]+)(/[^/?]+)+(\\?.*)?$",
                "$1$3"
        );
        if (!normalized.equals(jdbcUrl.trim())) {
            log.info("标准化 JDBC URL, 原值={}, 标准化后={}", jdbcUrl, normalized);
        }
        return normalized;
    }

    private String driverUrl(DatasourceType type) {
        String global = properties.getJdbcDriverUrl();
        return switch (type) {
            case POSTGRESQL -> firstNotBlank(properties.getPostgresqlJdbcDriverUrl(), firstNotBlank(global, POSTGRESQL_DRIVER_URL));
            case CLICKHOUSE -> firstNotBlank(properties.getClickhouseJdbcDriverUrl(), firstNotBlank(global, CLICKHOUSE_DRIVER_URL));
            case MYSQL, STARROCKS, DORIS -> firstNotBlank(properties.getMysqlJdbcDriverUrl(), firstNotBlank(global, MYSQL_DRIVER_URL));
            default -> global;
        };
    }

    private String driverClass(DatasourceType type) {
        String global = properties.getJdbcDriverClass();
        return switch (type) {
            case POSTGRESQL -> firstNotBlank(properties.getPostgresqlJdbcDriverClass(), firstNotBlank(global, POSTGRESQL_DRIVER_CLASS));
            case CLICKHOUSE -> firstNotBlank(properties.getClickhouseJdbcDriverClass(), firstNotBlank(global, CLICKHOUSE_DRIVER_CLASS));
            case MYSQL, STARROCKS, DORIS -> firstNotBlank(properties.getMysqlJdbcDriverClass(), firstNotBlank(global, MYSQL_DRIVER_CLASS));
            default -> firstNotBlank(global, MYSQL_DRIVER_CLASS);
        };
    }

    private String driverUrlConfigKey(DatasourceType type) {
        return switch (type) {
            case POSTGRESQL -> "starrocks.postgresql-jdbc-driver-url";
            case CLICKHOUSE -> "starrocks.clickhouse-jdbc-driver-url";
            case MYSQL, STARROCKS, DORIS -> "starrocks.mysql-jdbc-driver-url";
            default -> "starrocks.jdbc-driver-url";
        };
    }

    private String firstNotBlank(String first, String second) {
        return first == null || first.isBlank() ? second : first;
    }

    private boolean catalogNeedsRebuild(String catalogName, String user, String jdbcUri, String driverUrl, String driverClass) {
        String sql = "SHOW CREATE CATALOG " + quote(catalogName);
        try (Connection connection = connection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            if (!resultSet.next()) {
                return true;
            }
            String ddl = resultSet.getString(2);
            return !propertyEquals(ddl, "user", user)
                    || !propertyEquals(ddl, "jdbc_uri", jdbcUri)
                    || !propertyEquals(ddl, "driver_url", driverUrl)
                    || !propertyEquals(ddl, "driver_class", driverClass);
        } catch (Exception e) {
            log.info("StarRocks catalog 不存在或无法读取,将重新创建, catalog={}, err={}", catalogName, e.getMessage());
            return true;
        }
    }

    private boolean propertyEquals(String ddl, String key, String expected) {
        if (expected == null) {
            expected = "";
        }
        Matcher matcher = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*=\\s*\"([^\"]*)\"").matcher(ddl);
        return matcher.find() && expected.equals(matcher.group(1));
    }

    private void execute(String sql) {
        try (Connection connection = connection();
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (Exception e) {
            throw new SilentException("执行 StarRocks 管理 SQL 失败: " + e.getMessage());
        }
    }

    private Connection connection() throws java.sql.SQLException {
        return DriverManager.getConnection(properties.getUrl(), properties.getUsername(), properties.getPassword());
    }

    private String quote(String... identifiers) {
        return String.join(".", java.util.Arrays.stream(identifiers)
                .map(v -> "`" + v.replace("`", "``") + "`")
                .toList());
    }

    private String quoteList(List<String> columns) {
        return String.join(", ", columns.stream().map(this::quote).toList());
    }

    private String prop(String key, String value) {
        String safe = value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"");
        return "\"" + key + "\" = \"" + safe + "\"";
    }
}
