package com.cyan.stargaze.dataset.adapter.datasource.http.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 源表字段 DTO(探查返回)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ColumnDTO {

    /** 字段名 */
    private String name;

    /** 源库数据类型 */
    private String dataType;

    /** 字段注释 */
    private String comment;

    /** 是否可空 */
    private Boolean nullable;

    /** 是否主键 */
    private Boolean primaryKey;
}
