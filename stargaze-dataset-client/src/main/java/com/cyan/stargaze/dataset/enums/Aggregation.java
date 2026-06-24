package com.cyan.stargaze.dataset.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 度量字段聚合方式。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum Aggregation {

    /** 求和 */
    SUM("SUM", "求和"),
    /** 平均值 */
    AVG("AVG", "平均值"),
    /** 最大值 */
    MAX("MAX", "最大值"),
    /** 最小值 */
    MIN("MIN", "最小值"),
    /** 计数 */
    COUNT("COUNT", "计数"),
    /** 去重计数 */
    COUNT_DISTINCT("COUNT_DISTINCT", "去重计数");

    /** 聚合编码 */
    @EnumValue
    private final String code;
    /** 展示名称 */
    private final String displayName;
}
