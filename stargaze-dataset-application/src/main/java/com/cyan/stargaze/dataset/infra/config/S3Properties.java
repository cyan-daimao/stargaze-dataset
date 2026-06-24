package com.cyan.stargaze.dataset.infra.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * S3 兼容对象存储配置(rustfs/MinIO)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "s3")
public class S3Properties {

    /** 访问端点 */
    private String endpoint;

    /** 访问密钥 */
    private String accessKey;

    /** 私有密钥 */
    private String secretKey;

    /** 桶名 */
    private String bucket;

    /** 区域 */
    private String region = "us-east-1";

    /** 是否启用 path-style 访问(rustfs/MinIO 需 true) */
    private boolean pathStyleAccess = true;
}
