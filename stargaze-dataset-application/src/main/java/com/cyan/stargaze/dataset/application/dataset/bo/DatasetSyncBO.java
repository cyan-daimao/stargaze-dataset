package com.cyan.stargaze.dataset.application.dataset.bo;

import com.cyan.stargaze.dataset.enums.DatasetSyncStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;

/**
 * 数据集同步结果 BO(一期占位)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class DatasetSyncBO {

    /** 同步任务 ID */
    private String syncId;

    /** 数据集 ID */
    private String datasetId;

    /** 状态 */
    private DatasetSyncStatus status;

    /** 开始时间 */
    private OffsetDateTime startedAt;

    /** 预计完成时间 */
    private OffsetDateTime estimatedCompleteAt;

    /** 完成时间 */
    private OffsetDateTime completedAt;

    /** 耗时(秒) */
    private Long duration;

    /** 总行数 */
    private Long totalRows;

    /** 已同步行数 */
    private Long syncedRows;

    /** 错误信息 */
    private String errorMessage;
}
