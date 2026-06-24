package com.cyan.stargaze.dataset.adapter.dataset.http.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 表元信息 DTO(列表用,含注释/字段数/行数)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class TableMetaDTO {

    private String schema;
    private String tableName;
    private String tableComment;
    private Integer fieldCount;
    private Long rowCount;
}
