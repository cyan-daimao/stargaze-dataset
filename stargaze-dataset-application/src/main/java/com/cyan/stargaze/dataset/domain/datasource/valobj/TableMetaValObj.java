package com.cyan.stargaze.dataset.domain.datasource.valobj;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 表元信息值对象(富探查返回,含注释/字段数/行数)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class TableMetaValObj {

    /** 库/schema */
    private String schema;

    /** 表名 */
    private String tableName;

    /** 表注释 */
    private String tableComment;

    /** 字段数 */
    private Integer fieldCount;

    /** 行数估算 */
    private Long rowCount;
}
