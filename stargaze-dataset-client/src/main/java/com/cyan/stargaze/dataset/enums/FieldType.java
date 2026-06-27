package com.cyan.stargaze.dataset.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据集字段类型(物理字段类型)
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum FieldType {

    /** 维度(用于分组、切片) */
    DIMENSION("DIMENSION", "维度"),
    /** 度量(用于聚合计算) */
    MEASURE("MEASURE", "度量");

    /** 类型编码 */
    @EnumValue
    private final String code;
    /** 展示名称 */
    private final String displayName;
}
