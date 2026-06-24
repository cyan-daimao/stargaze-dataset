package com.cyan.stargaze.dataset.domain.dataset.config;

import com.cyan.stargaze.dataset.enums.JoinType;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * JOIN 子句:关联一张表 + 多个 AND 条件。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
public class JoinClause {

    /** 关联类型(LEFT_JOIN/INNER_JOIN/RIGHT_JOIN/FULL_JOIN) */
    private JoinType joinType;

    /** 被关联的表 */
    private TableRef table;

    /** 关联条件(AND 连接) */
    private List<JoinCondition> conditions;
}
