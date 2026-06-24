package com.cyan.stargaze.dataset.domain.dataset.config;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * JOIN 关联条件。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
public class JoinCondition {

    /** 左表关联字段 */
    private String leftField;

    /** 右表关联字段 */
    private String rightField;

    /** 比较运算符(一期仅支持 =) */
    private String operator;
}
