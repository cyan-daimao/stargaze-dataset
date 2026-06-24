package com.cyan.stargaze.dataset.infra.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * SQL 数据集执行配置(超时/行数上限)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "dataset.sql")
public class DatasetSqlProperties {

    /** 查询执行超时(秒) */
    private int queryTimeoutSeconds = 30;

    /** SQL 采样最大返回行数 */
    private int maxSampleRows = 1000;

    /** 数据集预览最大返回行数 */
    private int maxPreviewRows = 500;
}
