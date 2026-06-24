package com.cyan.stargaze.dataset.adapter.dataset.http.dto;

import com.cyan.stargaze.dataset.enums.CommonStatus;
import com.cyan.stargaze.dataset.enums.DatasetSourceType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * 数据集 DTO(返回前端,含字段列表)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class DatasetDTO {

    /** 主键 */
    private String id;

    /** 所属空间 ID */
    private String workspaceId;

    /** 数据集名称 */
    private String name;

    /** 来源类型 */
    private DatasetSourceType sourceType;

    /** 关联数据源 ID */
    private String dataSourceId;

    /** 来源定义(jsonb 字符串) */
    private String definition;

    /** 元数据刷新策略(jsonb 字符串) */
    private String refreshConfig;

    /** 物化加速配置(jsonb 字符串) */
    private String accelerations;

    /** 状态 */
    private CommonStatus status;

    /** 版本号 */
    private Integer version;

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

    /** 字段列表 */
    private List<DatasetFieldDTO> fields;
}
