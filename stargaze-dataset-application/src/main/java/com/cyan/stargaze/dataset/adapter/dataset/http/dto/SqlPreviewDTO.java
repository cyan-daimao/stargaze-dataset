package com.cyan.stargaze.dataset.adapter.dataset.http.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * SQL 预览结果 DTO(columns + rows 数组)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class SqlPreviewDTO {

    private List<SqlColumnDTO> columns;
    private List<List<Object>> rows;
    private Long totalRows;
    private Double executionTime;
    private Boolean isTruncated;
}
