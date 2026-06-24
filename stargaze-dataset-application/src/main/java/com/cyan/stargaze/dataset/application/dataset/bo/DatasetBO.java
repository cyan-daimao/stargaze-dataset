package com.cyan.stargaze.dataset.application.dataset.bo;

import com.cyan.stargaze.dataset.domain.dataset.config.DatasetConfig;
import com.cyan.stargaze.dataset.enums.DatasetSourceType;
import com.cyan.stargaze.dataset.enums.DatasetStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * 数据集业务对象(详情,含 config/fields/statistics/组装字段)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class DatasetBO {

    /** 主键 */
    private String id;

    /** 数据集名称 */
    private String name;

    /** 描述 */
    private String description;

    /** 来源类型 */
    private DatasetSourceType sourceType;

    /** 来源类型展示名 */
    private String sourceTypeName;

    /** 关联数据源 ID */
    private String datasourceId;

    /** 关联数据源名称 */
    private String datasourceName;

    /** 状态 */
    private DatasetStatus status;

    /** 版本号(整数) */
    private Integer version;

    /** 创建人 ID */
    private String createdBy;

    /** 创建人名称 */
    private String creator;

    /** 创建时间 */
    private OffsetDateTime createdAt;

    /** 更新时间 */
    private OffsetDateTime updatedAt;

    /** 来源配置(按类型) */
    private DatasetConfig config;

    /** 字段列表 */
    private List<DatasetFieldBO> fields;

    /** 统计信息 */
    private DatasetStatisticsBO statistics;
}
