package com.cyan.stargaze.dataset.domain.dataset.valobj;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 数据集字段统计值对象(总数/维度数/度量数)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class FieldCountStat {

    /** 数据集 ID */
    private String datasetId;

    /** 总字段数 */
    private Integer total;

    /** 维度字段数 */
    private Integer dimension;

    /** 度量字段数 */
    private Integer measure;
}
