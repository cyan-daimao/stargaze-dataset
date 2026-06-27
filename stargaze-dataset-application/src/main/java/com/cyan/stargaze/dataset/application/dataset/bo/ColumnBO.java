package com.cyan.stargaze.dataset.application.dataset.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 源表字段业务对象(探查返回)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ColumnBO {

    /** 字段名 */
    private String name;

    /** 源库数据类型 */
    private String dataType;

    /** 推断后的逻辑数据类型(供前端展示,如 VARCHAR/INT/DECIMAL/DATE) */
    private String suggestedType;

    /** 字段注释 */
    private String comment;

    /** 是否可空 */
    private Boolean nullable;

    /** 是否主键 */
    private Boolean primaryKey;
}
