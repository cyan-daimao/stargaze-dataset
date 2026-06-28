package com.cyan.stargaze.dataset.application.dataset.cmd;

import com.cyan.stargaze.dataset.enums.RefreshStrategy;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 物化加速配置命令。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class MaterializedViewCmd {

    /** 主键(更新时必填) */
    private String id;

    /** 所属数据集 ID */
    @NotBlank(message = "数据集 ID 不能为空")
    private String datasetId;

    /** StarRocks 物化表名 */
    @NotBlank(message = "物化表名不能为空")
    private String name;

    /** 是否启用 */
    private Boolean enabled;

    /** 目标引擎(默认 starrocks) */
    private String targetEngine;

    /** 目标库名 */
    private String targetDatabase;

    /** 目标表名 */
    private String targetTable;

    /** 刷新策略 */
    @NotNull(message = "刷新策略不能为空")
    private RefreshStrategy refreshStrategy;

    /** 刷新 cron 表达式 */
    private String refreshCron;

    /** 配置(jsonb 字符串:字段映射/分区/索引) */
    private String config;

    /** 创建人(controller 从 UserHolder 透传) */
    private String createdBy;

    /** 修改人(controller 从 UserHolder 透传) */
    private String updatedBy;
}
