package com.cyan.stargaze.dataset.domain.dataset.query;

import com.cyan.stargaze.dataset.enums.DatasetSourceType;
import com.cyan.stargaze.dataset.enums.DatasetStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 数据集列表分页查询条件。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class DatasetListQuery {

    /** 页码(1 起始) */
    private Integer page;

    /** 每页条数 */
    private Integer size;

    /** 名称关键词(模糊) */
    private String keyword;

    /** 来源类型 */
    private DatasetSourceType sourceType;

    /** 状态 */
    private DatasetStatus status;
}
