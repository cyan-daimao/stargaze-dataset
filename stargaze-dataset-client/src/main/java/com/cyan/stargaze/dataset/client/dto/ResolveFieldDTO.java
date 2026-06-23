package com.cyan.stargaze.dataset.client.dto;

import com.cyan.stargaze.dataset.enums.DataType;
import com.cyan.stargaze.dataset.enums.FieldType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 字段解析结果(供 metric 绑定字段时校验与类型推断)
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ResolveFieldDTO {

    /** 字段 ID */
    private String id;

    /** 物理字段名 */
    private String originName;

    /** 字段别名 */
    private String alias;

    /** 字段类型(dimension/measure) */
    private FieldType fieldType;

    /** 数据类型 */
    private DataType dataType;

    /** 所属数据源 ID */
    private String datasourceId;
}
