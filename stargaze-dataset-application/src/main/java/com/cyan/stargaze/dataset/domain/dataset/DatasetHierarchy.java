package com.cyan.stargaze.dataset.domain.dataset;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;

/**
 * 数据集维度层级领域对象(钻取)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class DatasetHierarchy {

    /** 主键 */
    private String id;

    /** 所属数据集 ID */
    private String datasetId;

    /** 层级名称 */
    private String name;

    /** 层级定义([{field_id,alias}] jsonb 序列化字符串) */
    private String levels;

    /** 创建时间 */
    private OffsetDateTime createdAt;

    /** 更新时间 */
    private OffsetDateTime updatedAt;

    /** 逻辑删除时间 */
    private OffsetDateTime deletedAt;
}
