package com.cyan.stargaze.dataset.infra.persistence.dataset.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;

/**
 * 数据集维度层级表 DO(dataset_hierarchy)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("dataset_hierarchy")
public class DatasetHierarchyDO {

    /** 主键 */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /** 所属数据集 ID */
    @TableField("dataset_id")
    private Long datasetId;

    /** 层级名称 */
    @TableField("name")
    private String name;

    /** 层级定义(jsonb) */
    @TableField("levels")
    private String levels;

    /** 创建人 */
    @TableField("created_by")
    private String createdBy;

    /** 修改人 */
    @TableField("updated_by")
    private String updatedBy;

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
