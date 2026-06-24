package com.cyan.stargaze.dataset.infra.persistence.datasource.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cyan.stargaze.dataset.enums.CommonStatus;
import com.cyan.stargaze.dataset.enums.DatasourceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;

/**
 * 数据源表 DO(data_source)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("data_source")
public class DataSourceDO {

    /** 主键 */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /** 所属空间 ID */
    @TableField("workspace_id")
    private Long workspaceId;

    /** 数据源名称 */
    @TableField("name")
    private String name;

    /** 数据源类型 */
    @TableField("type")
    private DatasourceType type;

    /** 连接配置(AES-256-GCM 加密后的 JSON 密文) */
    @TableField("config_enc")
    private String configEnc;

    /** 连接池配置(jsonb 序列化字符串) */
    @TableField("pool_config")
    private String poolConfig;

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
