package com.cyan.stargaze.dataset.domain.dataset;

import com.cyan.arch.common.api.Assert;
import com.cyan.arch.common.api.SilentException;
import com.cyan.stargaze.dataset.domain.dataset.repository.DatasetHierarchyRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;

/**
 * 数据集维度层级领域对象(钻取,充血模型)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class DatasetHierarchy {

    /** 主键 */
    private String id;

    /** 所属数据集 ID */
    private String datasetId;

    /** 层级名称 */
    private String name;

    /** 层级定义([{field_id,alias}] jsonb 序列化字符串) */
    private String levels;

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
        Assert.notBlank(this.name, new SilentException("层级名称不能为空"));
        Assert.notBlank(this.levels, new SilentException("层级定义不能为空"));
    }

    /**
     * 保存(新建)
     */
    public DatasetHierarchy save(DatasetHierarchyRepository repository) {
        validate();
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
        return repository.save(this);
    }

    /**
     * 更新
     */
    public DatasetHierarchy update(DatasetHierarchyRepository repository) {
        validate();
        Assert.notBlank(this.id, new SilentException("维度层级 ID 不能为空"));
        this.updatedAt = OffsetDateTime.now();
        return repository.update(this);
    }

    /**
     * 删除
     */
    public void delete(DatasetHierarchyRepository repository) {
        Assert.notBlank(this.id, new SilentException("维度层级 ID 不能为空"));
        repository.deleteById(this.id);
    }
}
