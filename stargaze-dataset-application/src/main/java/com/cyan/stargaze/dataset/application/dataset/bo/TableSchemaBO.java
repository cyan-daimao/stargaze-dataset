package com.cyan.stargaze.dataset.application.dataset.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 源表结构业务对象(探查返回,含字段列表与行数估算)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class TableSchemaBO {

    /** 表名 */
    private String tableName;

    /** 表注释 */
    private String tableComment;

    /** 行数估算 */
    private Long rowCount;

    /** 字段列表 */
    private List<ColumnBO> columns;
}
