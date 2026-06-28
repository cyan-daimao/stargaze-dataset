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
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
     * 确保 external catalog 存在。缺少 driver 配置时跳过创建,由查询阶段暴露明确错误。
     */
    public void ensureExternalCatalog(String catalogName, DataSource dataSource) {
        if (properties.getJdbcDriverUrl() == null || properties.getJdbcDriverUrl().isBlank()) {
            log.warn("StarRocks external catalog 未自动创建,缺少 starrocks.jdbc-driver-url, catalog={}", catalogName);
            return;
        }
        DataSourceConfig config = dataSource.getConfig();
        String jdbcUri = jdbcUri(dataSource.getType(), config);
        String driverClass = properties.getJdbcDriverClass();
        if (driverClass == null || driverClass.isBlank()) {
            driverClass = defaultDriverClass(dataSource.getType());
        }
        String sql = "CREATE EXTERNAL CATALOG IF NOT EXISTS " + quote(catalogName)
                + " PROPERTIES ("
                + prop("type", "jdbc") + ", "
                + prop("user", config.getUsername()) + ", "
                + prop("password", config.getPassword()) + ", "
                + prop("jdbc_uri", jdbcUri) + ", "
                + prop("driver_url", properties.getJdbcDriverUrl()) + ", "
                + prop("driver_class", driverClass)
                + ")";
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
            return config.getJdbcUrl();
        }
        String database = config.getDatabase() == null ? "" : "/" + config.getDatabase();
        return switch (type) {
            case POSTGRESQL -> "jdbc:postgresql://" + config.getHost() + ":" + config.getPort() + database;
            case MYSQL, STARROCKS, DORIS -> "jdbc:mysql://" + config.getHost() + ":" + config.getPort() + database;
            case CLICKHOUSE -> "jdbc:clickhouse://" + config.getHost() + ":" + config.getPort() + database;
            default -> throw new SilentException("暂不支持创建该数据源 catalog: " + type);
        };
    }

    private String defaultDriverClass(DatasourceType type) {
        return switch (type) {
            case POSTGRESQL -> "org.postgresql.Driver";
            case CLICKHOUSE -> "com.clickhouse.jdbc.ClickHouseDriver";
            default -> "com.mysql.cj.jdbc.Driver";
        };
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
