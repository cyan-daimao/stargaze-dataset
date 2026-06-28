package com.cyan.stargaze.dataset.infra.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * StarRocks 查询与物化配置。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "starrocks")
public class DatasetStarRocksProperties {

    /** JDBC URL */
    private String url;

    /** JDBC URL(url 的配置别名,兼容 starrocks.jdbc-url) */
    private String jdbcUrl;

    /** 用户名 */
    private String username;

    /** 密码 */
    private String password;

    /** 默认物化库 */
    private String database = "stargaze_materialized";

    /** Excel 表名前缀 */
    private String excelTablePrefix = "excel_ds_";

    /** 批量写入行数 */
    private int insertBatchSize = 1000;

    /** StarRocks JDBC catalog 驱动包地址 */
    private String jdbcDriverUrl;

    /** StarRocks JDBC catalog 驱动类名 */
    private String jdbcDriverClass;

    public String getUrl() {
        return url == null || url.isBlank() ? jdbcUrl : url;
    }
}
