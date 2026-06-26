package com.cyan.stargaze.dataset.domain.datasource.query;

import com.cyan.stargaze.dataset.enums.DatasourceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 数据源列表查询条件。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class DataSourceListQuery {

    /** 名称(模糊) */
    private String name;

    /** 数据源类型 */
    private DatasourceType type;
}
