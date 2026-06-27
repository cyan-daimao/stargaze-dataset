package com.cyan.stargaze.dataset.application.dataset.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Excel Sheet 业务对象。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ExcelSheetBO {

    /** Sheet 名称 */
    private String name;

    /** 数据行数(不含表头) */
    private Integer rowCount;
}
