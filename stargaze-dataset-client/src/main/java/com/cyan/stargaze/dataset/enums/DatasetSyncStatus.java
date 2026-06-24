package com.cyan.stargaze.dataset.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据集同步任务状态(一期占位,不入库)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum DatasetSyncStatus {

    /** 同步中 */
    RUNNING("RUNNING", "同步中"),
    /** 同步成功 */
    SUCCESS("SUCCESS", "同步成功"),
    /** 同步失败 */
    FAILED("FAILED", "同步失败");

    /** 状态编码 */
    private final String code;
    /** 展示名称 */
    private final String displayName;
}
