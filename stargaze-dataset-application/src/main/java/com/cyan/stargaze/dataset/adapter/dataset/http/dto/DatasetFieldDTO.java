package com.cyan.stargaze.dataset.adapter.dataset.http.dto;

import com.cyan.stargaze.dataset.enums.FieldType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 数据集字段 DTO(前端字段名)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class DatasetFieldDTO {

    /** 字段 ID */
    private String id;

    /** 物理字段名 */
    private String fieldName;

    /** 显示名称 */
    private String displayName;

    /** 数据类型(原始 DB 类型串) */
    private String dataType;

    /** 字段类型(维度/度量) */
    private FieldType fieldType;

    /** 是否启用 */
    private Boolean isEnabled;

    /** 来源表名 */
    private String sourceTable;

    /** 排序序号 */
    private Integer sortOrder;
}
