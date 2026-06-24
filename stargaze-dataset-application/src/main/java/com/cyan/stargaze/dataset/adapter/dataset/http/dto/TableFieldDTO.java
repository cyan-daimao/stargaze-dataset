package com.cyan.stargaze.dataset.adapter.dataset.http.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 表字段 DTO(探查返回)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class TableFieldDTO {

    private String fieldName;
    private String fieldType;
    private Boolean isNullable;
    private Boolean isPrimaryKey;
    private String fieldComment;
}
