package com.cyan.stargaze.dataset.application.dataset.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

/**
 * 源表采样业务对象(探查返回,默认 100 行)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class TableSampleBO {

    /** 列名列表 */
    private List<String> columns;

    /** 采样行(每行 列名->值) */
    private List<Map<String, Object>> rows;
}
