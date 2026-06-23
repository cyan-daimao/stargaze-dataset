package com.cyan.stargaze.dataset.application.dataset.cmd;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 数据集维度层级命令。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class DatasetHierarchyCmd {

    /** 主键(更新时必填) */
    private String id;

    /** 所属数据集 ID */
    @NotBlank(message = "数据集 ID 不能为空")
    private String datasetId;

    /** 层级名称 */
    @NotBlank(message = "层级名称不能为空")
    private String name;

    /** 层级定义(jsonb 字符串:[{field_id,alias}]) */
    @NotBlank(message = "层级定义不能为空")
    private String levels;
}
