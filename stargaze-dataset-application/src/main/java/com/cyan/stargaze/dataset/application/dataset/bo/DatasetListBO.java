package com.cyan.stargaze.dataset.application.dataset.bo;

import com.cyan.stargaze.dataset.enums.DatasetSourceType;
import com.cyan.stargaze.dataset.enums.DatasetStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;

/**
 * 数据集列表项业务对象。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class DatasetListBO {

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

    /** 总字段数 */
    private Integer fieldCount;

    /** 维度字段数 */
    private Integer dimensionCount;

    /** 度量字段数 */
    private Integer measureCount;

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

    /** 最近同步时间(占位取 updatedAt) */
    private OffsetDateTime lastSyncAt;
}
