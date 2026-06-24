package com.cyan.stargaze.dataset.domain.dataset;

import com.cyan.arch.common.api.Assert;
import com.cyan.arch.common.api.SilentException;
import com.cyan.stargaze.dataset.enums.DataType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;

/**
 * 数据集参数字段领域对象(可被图表/过滤引用的动态变量)。
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

    /** 创建时间 */
    private OffsetDateTime createdAt;

    /** 更新时间 */
    private OffsetDateTime updatedAt;

    /** 逻辑删除时间 */
    private OffsetDateTime deletedAt;

    /**
     * 校验
     */
    public void validate() {
        Assert.notBlank(this.name, new SilentException("参数名不能为空"));
        Assert.notNull(this.dataType, new SilentException("参数数据类型不能为空"));
    }
}
