package com.cyan.stargaze.dataset.application.dataset.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;

/**
 * 数据集统计信息 BO(详情用,best-effort)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class DatasetStatisticsBO {

    /** 总行数 */
    private Long totalRows;

    /** 上次同步行数 */
    private Long lastSyncRows;

    /** 上次同步时间 */
    private OffsetDateTime lastSyncAt;
}
