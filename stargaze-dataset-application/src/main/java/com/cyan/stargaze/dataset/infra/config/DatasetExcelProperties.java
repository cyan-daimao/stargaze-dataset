package com.cyan.stargaze.dataset.infra.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Excel 文件解析配置(大小/行列/采样上限)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "dataset.excel")
public class DatasetExcelProperties {

    /** 最大文件大小(字节),默认 50MB */
    private long maxFileSize = 52428800L;

    /** 最大数据行数 */
    private int maxRows = 100000;

    /** 最大列数 */
    private int maxColumns = 500;

    /** 采样返回行数 */
    private int sampleRows = 100;

    /** 类型推断采样行数 */
    private int inferRows = 100;
}
