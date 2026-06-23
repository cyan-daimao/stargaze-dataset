package com.cyan.stargaze.dataset.application.dataset.cmd;

import com.cyan.stargaze.dataset.enums.DatasetSourceType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 数据集创建/更新命令(含字段列表)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class DatasetCmd {

    /** 主键(更新时必填) */
    private String id;

    /** 所属空间 ID */
    @NotBlank(message = "空间 ID 不能为空")
    private String workspaceId;

    /** 数据集名称 */
    @NotBlank(message = "数据集名称不能为空")
    private String name;

    /** 来源类型 */
    @NotNull(message = "数据集来源类型不能为空")
    private DatasetSourceType sourceType;

    /** 关联数据源 ID */
    private String dataSourceId;

    /** 来源定义(jsonb 字符串:表名/SQL/JOIN 图/文件引用) */
    @NotBlank(message = "数据集来源定义不能为空")
    private String definition;

    /** 元数据刷新策略(jsonb 字符串) */
    private String refreshConfig;

    /** 物化加速配置(jsonb 字符串) */
    private String accelerations;

    /** 字段列表 */
    @Valid
    private List<DatasetFieldCmd> fields;

    /** 创建人(controller 从 UserHolder 透传) */
    private String createdBy;

    /** 修改人(controller 从 UserHolder 透传) */
    private String updatedBy;
}
