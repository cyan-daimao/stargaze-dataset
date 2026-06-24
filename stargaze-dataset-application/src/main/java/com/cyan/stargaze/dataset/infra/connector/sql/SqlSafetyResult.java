package com.cyan.stargaze.dataset.infra.connector.sql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * SQL 安全校验结果。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class SqlSafetyResult {

    /** 是否通过 */
    private boolean valid;

    /** 校验信息(通过为 OK,失败为原因) */
    private String message;

    /** 规范化后的 SQL(去尾分号,供执行使用,避免子查询拼接时尾分号导致语法错) */
    private String sanitized;

    public static SqlSafetyResult ok(String sanitized) {
        return new SqlSafetyResult().setValid(true).setMessage("OK").setSanitized(sanitized);
    }

    public static SqlSafetyResult fail(String message) {
        return new SqlSafetyResult().setValid(false).setMessage(message).setSanitized(null);
    }
}
