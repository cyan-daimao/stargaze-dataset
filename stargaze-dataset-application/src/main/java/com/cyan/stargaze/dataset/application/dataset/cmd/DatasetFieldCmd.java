package com.cyan.stargaze.dataset.application.dataset.cmd;

import com.cyan.stargaze.dataset.enums.DataType;
import com.cyan.stargaze.dataset.enums.FieldType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 数据集字段命令。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class DatasetFieldCmd {

    /** 物理字段名 */
    @NotBlank(message = "字段物理名不能为空")
    private String originName;

    /** 字段别名 */
    private String alias;

    /** 字段类型 */
    @NotNull(message = "字段类型不能为空")
    private FieldType fieldType;

    /** 数据类型 */
    @NotNull(message = "字段数据类型不能为空")
    private DataType dataType;

    /** 基础语义标注(jsonb 字符串) */
    private String semanticType;

    /** 格式(jsonb 字符串) */
    private String format;

    /** 字典 ID */
    private String dictionaryId;

    /** 是否隐藏 */
    private Boolean hidden;

    /** 排序序号 */
    private Integer ord;
}
