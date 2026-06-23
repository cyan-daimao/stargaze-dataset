package com.cyan.stargaze.dataset.adapter.datasource.http.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 源表结构 DTO(探查返回)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class TableSchemaDTO {

    /** 表名 */
    private String tableName;

    /** 表注释 */
    private String tableComment;

    /** 行数估算 */
    private Long rowCount;

    /** 字段列表 */
    private List<ColumnDTO> columns;
}
