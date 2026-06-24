package com.cyan.stargaze.dataset.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Excel 数据集数据更新方式(config 内使用,不入库)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum UpdateMode {

    /** 覆盖更新 */
    OVERWRITE("OVERWRITE", "覆盖更新"),
    /** 追加数据 */
    APPEND("APPEND", "追加数据");

    /** 编码 */
    private final String code;
    /** 展示名称 */
    private final String displayName;
}
