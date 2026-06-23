package com.cyan.stargaze.dataset.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 物化加速刷新策略
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum RefreshStrategy {

    /** 全量刷新 */
    FULL("full", "全量"),
    /** 增量刷新 */
    INCREMENTAL("incremental", "增量");

    /** 策略编码 */
    @EnumValue
    private final String code;
    /** 展示名称 */
    private final String displayName;
}
