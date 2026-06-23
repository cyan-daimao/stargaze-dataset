package com.cyan.stargaze.dataset.application.datasource.cmd;

import com.cyan.stargaze.dataset.domain.datasource.valobj.DataSourceConfig;
import com.cyan.stargaze.dataset.domain.datasource.valobj.PoolConfig;
import com.cyan.stargaze.dataset.enums.DatasourceType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 数据源创建/更新命令。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class DatasourceCmd {

    /** 主键(更新时必填) */
    private String id;

    /** 所属空间 ID */
    @NotBlank(message = "空间 ID 不能为空")
    private String workspaceId;

    /** 数据源名称 */
    @NotBlank(message = "数据源名称不能为空")
    private String name;

    /** 数据源类型 */
    @NotNull(message = "数据源类型不能为空")
    private DatasourceType type;

    /** 连接配置 */
    @Valid
    @NotNull(message = "数据源连接配置不能为空")
    private DataSourceConfig config;

    /** 连接池配置 */
    @Valid
    private PoolConfig poolConfig;

    /** 创建人(controller 从 UserHolder 透传) */
    private String createdBy;

    /** 修改人(controller 从 UserHolder 透传) */
    private String updatedBy;
}
