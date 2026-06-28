package com.cyan.stargaze.dataset.infra.persistence.dataset.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cyan.stargaze.dataset.domain.dataset.MaterializedView;
import com.cyan.stargaze.dataset.enums.RefreshStrategy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;

/**
 * 物化加速配置表 DO(materialized_view)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("materialized_view")
public class MaterializedViewDO {

    /** 主键 */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /** 所属数据集 ID */
    @TableField("dataset_id")
    private Long datasetId;

    /** StarRocks 物化表名 */
    @TableField("name")
    private String name;

    /** 是否启用 */
    @TableField("enabled")
    private Boolean enabled;

    /** 目标引擎 */
    @TableField("target_engine")
    private String targetEngine;

    /** 目标库名 */
    @TableField("target_database")
    private String targetDatabase;

    /** 目标表名 */
    @TableField("target_table")
    private String targetTable;

    /** 刷新策略(full/incremental) */
    @TableField("refresh_strategy")
    private RefreshStrategy refreshStrategy;

    /** 刷新 cron 表达式 */
    @TableField("refresh_cron")
    private String refreshCron;

    /** 最近同步时间 */
    @TableField("last_sync_at")
    private OffsetDateTime lastSyncAt;

    /** 同步状态(idle/syncing/error) */
    @TableField("status")
    private MaterializedView.SyncStatus status;

    /** 配置(jsonb) */
    @TableField("config")
    private String config;

    /** 最近同步错误 */
    @TableField("last_error")
    private String lastError;

    /** 创建人 */
    @TableField("created_by")
    private Long createdBy;

    /** 修改人 */
    @TableField("updated_by")
    private Long updatedBy;

    /** 创建时间 */
    @TableField("created_at")
    private OffsetDateTime createdAt;

    /** 更新时间 */
    @TableField("updated_at")
    private OffsetDateTime updatedAt;

    /** 逻辑删除时间 */
    @TableField("deleted_at")
    @TableLogic(value = "null", delval = "now()")
    private OffsetDateTime deletedAt;
}
