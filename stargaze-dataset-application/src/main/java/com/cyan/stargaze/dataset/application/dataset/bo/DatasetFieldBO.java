package com.cyan.stargaze.dataset.application.dataset.bo;

import com.cyan.stargaze.dataset.enums.Aggregation;
import com.cyan.stargaze.dataset.enums.FieldType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 数据集字段业务对象。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class DatasetFieldBO {

    /** 主键 */
    private String id;

    /** 所属数据集 ID */
    private String datasetId;

    /** 物理字段名 */
    private String fieldName;

    /** 显示名称 */
    private String displayName;

    /** 字段类型(dimension/measure) */
    private FieldType fieldType;

    /** 数据类型(原始 DB 类型串) */
    private String dataType;

    /** 聚合方式 */
    private Aggregation aggregation;

    /** 来源表名 */
    private String sourceTable;

    /** 是否启用 */
    private Boolean isEnabled;

    /** 排序序号 */
    private Integer sortOrder;
}
