package com.cyan.stargaze.dataset.domain.dataset;

import com.cyan.arch.common.api.Assert;
import com.cyan.arch.common.api.SilentException;
import com.cyan.stargaze.dataset.enums.DataType;
import com.cyan.stargaze.dataset.enums.FieldType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;

/**
 * 数据集字段领域对象(纯物理字段,不含 expr/mask_rule)。
 * <p>
 * 派生计算上移 metric.dsl,脱敏上移 field_mask_rule。本字段是「干净的物理字段」,
 * 被指标/维度 binding 引用。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class DatasetField {

    /** 主键 */
    private String id;

    /** 所属数据集 ID */
    private String datasetId;

    /** 物理字段名(源表真实列名) */
    private String originName;

    /** 字段别名 */
    private String alias;

    /** 字段类型(dimension/measure) */
    private FieldType fieldType;

    /** 数据类型(string/int/decimal/date/datetime/boolean) */
    private DataType dataType;

    /** 基础语义标注(geo/time/category,jsonb 序列化字符串,可空) */
    private String semanticType;

    /** 格式(数字/日期,jsonb 序列化字符串,可空) */
    private String format;

    /** 字典 ID */
    private String dictionaryId;

    /** 是否隐藏 */
    private Boolean hidden;

    /** 排序序号 */
    private Integer ord;

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
        Assert.notBlank(this.originName, new SilentException("字段物理名不能为空"));
        Assert.notNull(this.fieldType, new SilentException("字段类型不能为空"));
        Assert.notNull(this.dataType, new SilentException("字段数据类型不能为空"));
    }

    /**
     * 是否维度
     */
    public boolean isDimension() {
        return FieldType.DIMENSION == this.fieldType;
    }

    /**
     * 是否度量
     */
    public boolean isMeasure() {
        return FieldType.MEASURE == this.fieldType;
    }
}
