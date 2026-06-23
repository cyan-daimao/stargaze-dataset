package com.cyan.stargaze.dataset.infra.persistence.dataset.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cyan.stargaze.dataset.enums.DataType;
import com.cyan.stargaze.dataset.enums.FieldType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 数据集字段表 DO(dataset_field)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("dataset_field")
public class DatasetFieldDO {

    /** 主键 */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /** 所属数据集 ID */
    @TableField("dataset_id")
    private Long datasetId;

    /** 物理字段名 */
    @TableField("origin_name")
    private String originName;

    /** 字段别名 */
    @TableField("alias")
    private String alias;

    /** 字段类型(dimension/measure) */
    @TableField("field_type")
    private FieldType fieldType;

    /** 数据类型 */
    @TableField("data_type")
    private DataType dataType;

    /** 基础语义标注(jsonb) */
    @TableField("semantic_type")
    private String semanticType;

    /** 格式(jsonb) */
    @TableField("format")
    private String format;

    /** 字典 ID */
    @TableField("dictionary_id")
    private Long dictionaryId;

    /** 是否隐藏 */
    @TableField("hidden")
    private Boolean hidden;

    /** 排序序号 */
    @TableField("ord")
    private Integer ord;

    /** 创建时间 */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /** 更新时间 */
    @TableField("updated_at")
    private LocalDateTime updatedAt;

    /** 逻辑删除时间 */
    @TableField("deleted_at")
    @TableLogic(value = "null", delval = "now()")
    private LocalDateTime deletedAt;
}
