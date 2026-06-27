package com.cyan.stargaze.dataset.application.dataset.bo;

import com.cyan.stargaze.dataset.domain.dataset.MaterializedView;
import com.cyan.stargaze.dataset.enums.RefreshStrategy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;

/**
 * 物化加速配置业务对象。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class MaterializedViewBO {

    /** 主键 */
    private String id;

    /** 所属数据集 ID */
    private String datasetId;

    /** StarRocks 物化表名 */
    private String name;

    /** 目标引擎(默认 starrocks) */
    private String targetEngine;

    /** 刷新策略(full/incremental) */
    private RefreshStrategy refreshStrategy;

    /** 刷新 cron 表达式 */
    private String refreshCron;

    /** 最近同步时间 */
    private OffsetDateTime lastSyncAt;

    /** 同步状态 */
    private MaterializedView.SyncStatus status;

    /** 配置(字段映射/分区/索引,jsonb 序列化字符串) */
    private String config;

    /** 创建人 */
    private String createdBy;

    /** 修改人 */
    private String updatedBy;

    /** 创建时间 */
    private OffsetDateTime createdAt;

    /** 更新时间 */
    private OffsetDateTime updatedAt;
}
