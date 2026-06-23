package com.cyan.stargaze.dataset.domain.dataset.query;

import com.cyan.stargaze.dataset.enums.DatasetSourceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 数据集列表查询条件。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class DatasetListQuery {

    /** 空间 ID */
    private String workspaceId;

    /** 名称(模糊) */
    private String name;

    /** 来源类型 */
    private DatasetSourceType sourceType;

    /** 数据源 ID */
    private String dataSourceId;
}
