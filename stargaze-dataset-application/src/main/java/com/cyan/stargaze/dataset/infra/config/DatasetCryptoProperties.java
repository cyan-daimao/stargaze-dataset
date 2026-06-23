package com.cyan.stargaze.dataset.infra.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 数据源连接密码加密配置(AES-256-GCM,密钥由 KMS 托管)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "dataset.crypto")
public class DatasetCryptoProperties {

    /** AES 加密密钥(base64 或明文,生产由 KMS/环境变量注入) */
    private String aesKey;
}
