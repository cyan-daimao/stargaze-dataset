package com.cyan.stargaze.dataset.domain.dataset;

import com.cyan.arch.common.api.Assert;
import com.cyan.arch.common.api.SilentException;
import com.cyan.stargaze.dataset.domain.dataset.repository.MaterializedViewRepository;
import com.cyan.stargaze.dataset.enums.RefreshStrategy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;

/**
 * 物化加速配置领域对象(充血模型)。
 * <p>
 * 物化表实际存 StarRocks,本对象仅存加速配置。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class MaterializedView {

    /** 物化状态:idle/syncing/success/error */
    public enum SyncStatus {
        IDLE, SYNCING, SUCCESS, ERROR
    }

    /** 主键 */
    private String id;

    /** 所属数据集 ID */
    private String datasetId;

    /** StarRocks 物化表名 */
    private String name;

    /** 是否启用 */
    private Boolean enabled;

    /** 目标引擎(默认 starrocks) */
    private String targetEngine;

    /** 目标库名 */
    private String targetDatabase;

    /** 目标表名 */
    private String targetTable;

    /** 刷新策略(full/incremental) */
    private RefreshStrategy refreshStrategy;

    /** 刷新 cron 表达式 */
    private String refreshCron;

    /** 最近同步时间 */
    private OffsetDateTime lastSyncAt;

    /** 同步状态 */
    private SyncStatus status;

    /** 配置(字段映射/分区/索引,jsonb 序列化字符串) */
    private String config;

    /** 最近同步错误 */
    private String lastError;

    /** 创建人 */
    private String createdBy;

    /** 修改人 */
    private String updatedBy;

    /** 创建时间 */
    private OffsetDateTime createdAt;

    /** 更新时间 */
    private OffsetDateTime updatedAt;

    /** 逻辑删除时间 */
    private OffsetDateTime deletedAt;

    /**
     * 校验
     */
    private void validate() {
        Assert.notBlank(this.datasetId, new SilentException("数据集 ID 不能为空"));
        Assert.notBlank(this.name, new SilentException("物化表名不能为空"));
        Assert.notNull(this.refreshStrategy, new SilentException("刷新策略不能为空"));
    }

    /**
     * 保存
     */
    public MaterializedView save(MaterializedViewRepository repository) {
        validate();
        this.enabled = this.enabled == null || this.enabled;
        this.targetEngine = this.targetEngine == null ? "starrocks" : this.targetEngine;
        this.targetTable = this.targetTable == null ? this.name : this.targetTable;
        this.status = this.status == null ? SyncStatus.IDLE : this.status;
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
        return repository.save(this);
    }

    /**
     * 更新
     */
    public MaterializedView update(MaterializedViewRepository repository) {
        validate();
        Assert.notBlank(this.id, new SilentException("物化加速配置 ID 不能为空"));
        this.updatedAt = OffsetDateTime.now();
        return repository.update(this);
    }

    /**
     * 标记同步中
     */
    public void markSyncing() {
        this.status = SyncStatus.SYNCING;
        this.updatedAt = OffsetDateTime.now();
    }

    /**
     * 标记同步完成
     */
    public void markSynced() {
        this.status = SyncStatus.SUCCESS;
        this.lastSyncAt = OffsetDateTime.now();
        this.lastError = null;
        this.updatedAt = OffsetDateTime.now();
    }

    /**
     * 标记同步失败
     */
    public void markError(String error) {
        this.status = SyncStatus.ERROR;
        this.lastError = error;
        this.updatedAt = OffsetDateTime.now();
    }

    /**
     * 删除
     */
    public void delete(MaterializedViewRepository repository) {
        Assert.notBlank(this.id, new SilentException("物化加速配置 ID 不能为空"));
        repository.deleteById(this.id);
    }
}
