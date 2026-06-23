package com.cyan.stargaze.dataset.domain.datasource.valobj;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 数据库/schema 值对象(探查返回)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class DatabaseValObj {

    /** 库/schema 名 */
    private String name;

    /** 默认字符集 */
    private String charset;

    /** 注释 */
    private String comment;
}
