package com.cyan.stargaze.dataset.domain.dataset;

import com.cyan.arch.common.api.Assert;
import com.cyan.arch.common.api.SilentException;
import com.cyan.stargaze.dataset.domain.dataset.repository.DatasetParameterRepository;
import com.cyan.stargaze.dataset.enums.DataType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;

/**
 * 数据集参数字段领域对象(可被图表/过滤引用的动态变量,充血模型)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class DatasetParameter {

    /** 主键 */
    private String id;

    /** 所属数据集 ID */
    private String datasetId;

    /** 参数名(数据集内唯一) */
    private String name;

    /** 参数别名 */
    private String alias;

    /** 数据类型 */
    private DataType dataType;

    /** 默认值 */
    private String defaultValue;

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
        Assert.notBlank(this.name, new SilentException("参数名不能为空"));
        Assert.notNull(this.dataType, new SilentException("参数数据类型不能为空"));
    }

    /**
     * 保存(新建)
     */
    public DatasetParameter save(DatasetParameterRepository repository) {
        validate();
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
        return repository.save(this);
    }

    /**
     * 更新
     */
    public DatasetParameter update(DatasetParameterRepository repository) {
        validate();
        Assert.notBlank(this.id, new SilentException("参数字段 ID 不能为空"));
        this.updatedAt = OffsetDateTime.now();
        return repository.update(this);
    }

    /**
     * 删除
     */
    public void delete(DatasetParameterRepository repository) {
        Assert.notBlank(this.id, new SilentException("参数字段 ID 不能为空"));
        repository.deleteById(this.id);
    }
}
