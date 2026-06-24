package com.cyan.stargaze.dataset.application.dataset.bo;

import com.cyan.stargaze.dataset.domain.datasource.valobj.ColumnValObj;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

/**
 * SQL 预览结果 BO。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class SqlPreviewBO {

    /** 列(含 name/dataType) */
    private List<ColumnValObj> columns;

    /** 采样行(列名 -> 值) */
    private List<Map<String, Object>> rows;

    /** 总行数(本次返回) */
    private Long totalRows;

    /** 执行耗时(秒) */
    private Double executionTime;

    /** 是否截断 */
    private Boolean isTruncated;
}
