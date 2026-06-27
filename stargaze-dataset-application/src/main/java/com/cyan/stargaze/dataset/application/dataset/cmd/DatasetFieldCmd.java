package com.cyan.stargaze.dataset.application.dataset.cmd;

import com.cyan.stargaze.dataset.enums.FieldType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 数据集字段命令(前端字段名)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class DatasetFieldCmd {

    /** 字段 ID(更新时) */
    private String id;

    /** 物理字段名(原始列名) */
    @NotBlank(message = "字段名不能为空")
    private String fieldName;

    /** 显示名称 */
    private String displayName;

    /** 数据类型(原始 DB 类型串,如 BIGINT/VARCHAR) */
    @NotBlank(message = "字段数据类型不能为空")
    private String dataType;

    /** 字段类型(维度/度量) */
    @NotNull(message = "字段类型不能为空")
    private FieldType fieldType;

    /** 来源表名(多表关联时) */
    private String sourceTable;

    /** 是否启用 */
    private Boolean isEnabled;

    /** 排序序号 */
    private Integer sortOrder;
}
