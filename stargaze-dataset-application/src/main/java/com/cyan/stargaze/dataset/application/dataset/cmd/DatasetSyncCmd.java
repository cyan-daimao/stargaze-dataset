package com.cyan.stargaze.dataset.application.dataset.cmd;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 数据集同步命令(一期占位,字段接收但不影响逻辑)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class DatasetSyncCmd {

    /** 同步类型(FULL/INCREMENTAL) */
    private String syncType;

    /** 增量字段 */
    private String incrementalField;

    /** 增量值 */
    private String incrementalValue;
}
