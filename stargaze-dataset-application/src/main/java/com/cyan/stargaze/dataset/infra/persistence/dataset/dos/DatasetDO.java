package com.cyan.stargaze.dataset.infra.persistence.dataset.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cyan.stargaze.dataset.enums.DatasetSourceType;
import com.cyan.stargaze.dataset.enums.DatasetStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;

/**
 * 数据集表 DO(dataset)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("dataset")
public class DatasetDO {

    /** 主键 */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /** 数据集名称 */
    @TableField("name")
    private String name;

    /** 描述 */
    @TableField("description")
    private String description;

    /** 来源类型(table/sql/join/excel/union) */
    @TableField("source_type")
    private DatasetSourceType sourceType;

    /** 关联数据源 ID */
    @TableField("data_source_id")
    private Long dataSourceId;

    /** 来源定义(jsonb) */
    @TableField("definition")
    private String definition;

    /** 元数据刷新策略(jsonb) */
    @TableField("refresh_config")
    private String refreshConfig;

    /** 物化加速配置(jsonb) */
    @TableField("accelerations")
    private String accelerations;

    /** 状态 */
    @TableField("status")
    private DatasetStatus status;

    /** 版本号 */
    @TableField("version")
    private Integer version;

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
