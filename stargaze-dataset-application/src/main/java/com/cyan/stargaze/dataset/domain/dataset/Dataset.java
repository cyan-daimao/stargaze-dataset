package com.cyan.stargaze.dataset.domain.dataset;

import com.cyan.arch.common.api.Assert;
import com.cyan.arch.common.api.SilentException;
import com.cyan.stargaze.dataset.domain.dataset.repository.DatasetRepository;
import com.cyan.stargaze.dataset.enums.CommonStatus;
import com.cyan.stargaze.dataset.enums.DatasetSourceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 数据集领域对象(充血模型,聚合字段列表)。
 * <p>
 * 数据集是指标的物理来源,只定义物理字段与基础语义,业务口径上移到指标层。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Dataset {

    /** 主键 */
    private String id;

    /** 所属空间 ID */
    private String workspaceId;

    /** 数据集名称(空间内唯一) */
    private String name;

    /** 来源类型(table/sql/join/excel/union) */
    private DatasetSourceType sourceType;

    /** 关联数据源 ID */
    private String dataSourceId;

    /** 来源定义(表名/SQL/JOIN 图/文件引用,jsonb 序列化字符串) */
    private String definition;

    /** 元数据刷新策略(jsonb 序列化字符串) */
    private String refreshConfig;

    /** 物化加速配置(jsonb 序列化字符串) */
    private String accelerations;

    /** 状态:active/error/inactive */
    private CommonStatus status;

    /** 版本号 */
    private Integer version;

    /** 创建人 */
    private String createdBy;

    /** 修改人 */
    private String updatedBy;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;

    /** 逻辑删除时间 */
    private LocalDateTime deletedAt;

    /** 字段列表(聚合子实体) */
    private List<DatasetField> fields;

    /**
     * 校验
     */
    private void validate() {
        Assert.notBlank(this.workspaceId, new SilentException("空间 ID 不能为空"));
        Assert.notBlank(this.name, new SilentException("数据集名称不能为空"));
        Assert.notNull(this.sourceType, new SilentException("数据集来源类型不能为空"));
        Assert.notBlank(this.definition, new SilentException("数据集来源定义不能为空"));
    }

    /**
     * 保存(新建,连同字段一起持久化)
     */
    public Dataset save(DatasetRepository repository) {
        validate();
        Dataset existing = repository.findByName(this.workspaceId, this.name);
        Assert.isNull(existing, new SilentException("数据集名称已存在"));
        // 字段逐一校验
        if (fields != null) {
            fields.forEach(DatasetField::validate);
        }
        this.status = CommonStatus.ACTIVE;
        this.version = 1;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        return repository.save(this);
    }

    /**
     * 更新(版本号递增)
     */
    public Dataset update(DatasetRepository repository) {
        validate();
        Assert.notBlank(this.id, new SilentException("数据集 ID 不能为空"));
        if (fields != null) {
            fields.forEach(DatasetField::validate);
        }
        this.version = (this.version == null ? 1 : this.version) + 1;
        this.updatedAt = LocalDateTime.now();
        return repository.update(this);
    }

    /**
     * 删除(逻辑删除,连带字段)
     */
    public void delete(DatasetRepository repository) {
        Assert.notBlank(this.id, new SilentException("数据集 ID 不能为空"));
        repository.deleteById(this.id);
    }
}
