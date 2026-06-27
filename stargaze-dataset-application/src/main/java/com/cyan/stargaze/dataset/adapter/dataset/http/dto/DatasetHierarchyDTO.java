package com.cyan.stargaze.dataset.adapter.dataset.http.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;

/**
 * 数据集维度层级返回 DTO。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class DatasetHierarchyDTO {

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
}
