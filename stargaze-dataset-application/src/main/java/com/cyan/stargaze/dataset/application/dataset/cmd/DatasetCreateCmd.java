package com.cyan.stargaze.dataset.application.dataset.cmd;

import com.cyan.stargaze.dataset.domain.dataset.config.ExcelConfig;
import com.cyan.stargaze.dataset.domain.dataset.config.JoinConfig;
import com.cyan.stargaze.dataset.domain.dataset.config.SqlConfig;
import com.cyan.stargaze.dataset.domain.dataset.config.TableConfig;
import com.cyan.stargaze.dataset.enums.DatasetSourceType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 数据集创建命令(按 sourceType 携带对应 config)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class DatasetCreateCmd {

    /** 数据集名称 */
    @NotBlank(message = "数据集名称不能为空")
    private String name;

    /** 描述 */
    private String description;

    /** 来源类型 */
    @NotNull(message = "数据集来源类型不能为空")
    private DatasetSourceType sourceType;

    /** 关联数据源 ID(table/sql/join 必填) */
    private String datasourceId;

    /** 表配置(sourceType=TABLE) */
    @Valid
    private TableConfig tableConfig;

    /** SQL 配置(sourceType=SQL) */
    @Valid
    private SqlConfig sqlConfig;

    /** JOIN 配置(sourceType=JOIN) */
    @Valid
    private JoinConfig joinConfig;

    /** Excel 配置(sourceType=EXCEL) */
    @Valid
    private ExcelConfig excelConfig;

    /** 字段列表(可空,空则自动解析) */
    @Valid
    private List<DatasetFieldCmd> fields;

    /** 创建人(controller 从 UserHolder 透传) */
    private String createdBy;
}
