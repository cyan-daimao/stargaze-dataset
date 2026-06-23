package com.cyan.stargaze.dataset.domain.datasource.valobj;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 数据源连接配置值对象(明文,持久化前由 infra 层 AES-256-GCM 加密为 config_enc)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class DataSourceConfig {

    /** 主机 */
    private String host;

    /** 端口 */
    private Integer port;

    /** 库名/schema */
    private String database;

    /** 用户名 */
    private String username;

    /** 密码(明文,仅存在于领域/应用层内存,不落库) */
    private String password;

    /** 认证方式:basic / kerberos / cloud_ak_sk */
    private String authType;

    /** 完整 JDBC URL(优先于 host+port+database 拼接) */
    private String jdbcUrl;

    /** 额外参数 */
    private String extraParams;
}
