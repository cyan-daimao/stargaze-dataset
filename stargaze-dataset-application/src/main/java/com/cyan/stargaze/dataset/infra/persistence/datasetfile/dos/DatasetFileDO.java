package com.cyan.stargaze.dataset.infra.persistence.datasetfile.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cyan.stargaze.dataset.enums.CommonStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;

/**
 * 数据集文件表 DO(dataset_file)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("dataset_file")
public class DatasetFileDO {

    /** 主键 */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /** 所属空间 ID */
    @TableField("workspace_id")
    private Long workspaceId;

    /** 原始文件名 */
    @TableField("file_name")
    private String fileName;

    /** S3 对象 key */
    @TableField("object_key")
    private String objectKey;

    /** 文件类型 */
    @TableField("content_type")
    private String contentType;

    /** 文件大小(字节) */
    @TableField("size")
    private Long size;

    /** 状态 */
    @TableField("status")
    private CommonStatus status;

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
