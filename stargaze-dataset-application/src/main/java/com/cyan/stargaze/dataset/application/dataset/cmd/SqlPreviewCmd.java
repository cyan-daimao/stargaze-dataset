package com.cyan.stargaze.dataset.application.dataset.cmd;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * SQL 预览命令。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class SqlPreviewCmd {

    /** 数据源 ID */
    @NotBlank(message = "数据源 ID 不能为空")
    private String datasourceId;

    /** SQL */
    @NotBlank(message = "SQL 不能为空")
    private String sql;

    /** 返回行数上限(默认 100) */
    private Integer limit;
}
