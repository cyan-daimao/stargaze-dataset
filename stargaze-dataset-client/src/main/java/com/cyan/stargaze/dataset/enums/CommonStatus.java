package com.cyan.stargaze.dataset.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通用启用状态(数据源/数据集/物化加速等)
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum CommonStatus {

    /** 正常/活跃 */
    ACTIVE("ACTIVE", "正常"),
    /** 异常 */
    ERROR("ERROR", "异常"),
    /** 停用 */
    INACTIVE("INACTIVE", "停用");

    /** 状态编码 */
    @EnumValue
    private final String code;
    /** 展示名称 */
    private final String displayName;
}
