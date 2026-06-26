package com.cyan.stargaze.dataset.client.dto;

import com.cyan.stargaze.dataset.enums.DataType;
import com.cyan.stargaze.dataset.enums.FieldType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 数据集字段 DTO(对外契约,供 metric 等服务绑定字段校验)
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

    /** 所属数据集 ID */
    private String datasetId;

    /** 物理字段名(源表真实列名) */
    private String originName;

    /** 字段别名 */
    private String alias;

    /** 字段类型(dimension/measure) */
    private FieldType fieldType;

    /** 数据类型(string/int/decimal/date/datetime/boolean) */
    private DataType dataType;

    /** 基础语义标注(geo/time/category,jsonb 序列化字符串) */
    private String semanticType;

    /** 排序序号 */
    private Integer ord;
}
