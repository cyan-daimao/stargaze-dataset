package com.cyan.stargaze.dataset.application.dataset.bo;

import com.cyan.stargaze.dataset.enums.DataType;
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
    private String originName;

    /** 字段别名 */
    private String alias;

    /** 字段类型(dimension/measure) */
    private FieldType fieldType;

    /** 数据类型 */
    private DataType dataType;

    /** 基础语义标注(jsonb 字符串) */
    private String semanticType;

    /** 格式(jsonb 字符串) */
    private String format;

    /** 字典 ID */
    private String dictionaryId;

    /** 是否隐藏 */
    private Boolean hidden;

    /** 排序序号 */
    private Integer ord;
}
