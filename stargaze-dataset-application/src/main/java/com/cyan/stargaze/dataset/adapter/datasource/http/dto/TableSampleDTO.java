package com.cyan.stargaze.dataset.adapter.datasource.http.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

/**
 * 源表采样 DTO(探查/预览返回)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class TableSampleDTO {

    /** 列名列表 */
    private List<String> columns;

    /** 采样行 */
    private List<Map<String, Object>> rows;
}
