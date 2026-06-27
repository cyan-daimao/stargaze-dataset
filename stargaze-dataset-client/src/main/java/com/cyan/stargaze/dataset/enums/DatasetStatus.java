package com.cyan.stargaze.dataset.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据集状态(发布生命周期)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum DatasetStatus {

    /** 草稿 */
    DRAFT("DRAFT", "草稿"),
    /** 已发布 */
    PUBLISHED("PUBLISHED", "已发布"),
    /** 已归档 */
    ARCHIVED("ARCHIVED", "已归档");

    /** 状态编码 */
    @EnumValue
    private final String code;
    /** 展示名称 */
    private final String displayName;
}
