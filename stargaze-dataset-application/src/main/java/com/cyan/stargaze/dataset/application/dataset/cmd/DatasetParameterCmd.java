package com.cyan.stargaze.dataset.application.dataset.cmd;

import com.cyan.stargaze.dataset.enums.DataType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 数据集参数字段命令。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class DatasetParameterCmd {

    /** 主键(更新时必填) */
    private String id;

    /** 所属数据集 ID */
    @NotBlank(message = "数据集 ID 不能为空")
    private String datasetId;

    /** 参数名 */
    @NotBlank(message = "参数名不能为空")
    private String name;

    /** 参数别名 */
    private String alias;

    /** 数据类型 */
    @NotNull(message = "参数数据类型不能为空")
    private DataType dataType;

    /** 默认值 */
    private String defaultValue;
}
