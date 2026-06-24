package com.cyan.stargaze.dataset.domain.dataset.valobj;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Excel sheet 元信息值对象(探查返回)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ExcelSheetValObj {

    /** sheet 名称 */
    private String name;

    /** 数据行数(不含表头) */
    private Integer rowCount;
}
