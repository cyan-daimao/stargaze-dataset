package com.cyan.stargaze.dataset.application.dataset.bo;

import com.cyan.stargaze.dataset.enums.CommonStatus;
import com.cyan.stargaze.dataset.enums.DatasetSourceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * 数据集业务对象(含字段列表)。
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
    private OffsetDateTime createdAt;

    /** 更新时间 */
    private OffsetDateTime updatedAt;

    /** 字段列表 */
    private List<DatasetFieldBO> fields;
}
