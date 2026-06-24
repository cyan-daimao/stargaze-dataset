package com.cyan.stargaze.dataset.adapter.datasource.http.dto;

import com.cyan.stargaze.dataset.domain.datasource.valobj.DataSourceConfig;
import com.cyan.stargaze.dataset.domain.datasource.valobj.PoolConfig;
import com.cyan.stargaze.dataset.enums.CommonStatus;
import com.cyan.stargaze.dataset.enums.DatasourceType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;

/**
 * 数据源 DTO(返回前端,密码脱敏)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class DatasourceDTO {

    /** 主键 */
    private String id;

    /** 所属空间 ID */
    private String workspaceId;

    /** 数据源名称 */
    private String name;

    /** 数据源类型 */
    private DatasourceType type;

    /** 连接配置(密码脱敏) */
    private DataSourceConfig config;

    /** 连接池配置 */
    private PoolConfig poolConfig;

    /** 状态 */
    private CommonStatus status;

    /** 创建人 */
    private String createdBy;

    /** 修改人 */
    private String updatedBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private OffsetDateTime createdAt;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private OffsetDateTime updatedAt;
}
