package com.cyan.stargaze.dataset.adapter.datasource.http.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 库/schema DTO。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class DatabaseDTO {

    /** 库/schema 名 */
    private String name;

    /** 注释 */
    private String comment;
}
