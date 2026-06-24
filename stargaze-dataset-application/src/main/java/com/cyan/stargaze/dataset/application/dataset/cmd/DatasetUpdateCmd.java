package com.cyan.stargaze.dataset.application.dataset.cmd;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 数据集更新命令(名称/描述/字段配置)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class DatasetUpdateCmd {

    /** 数据集名称 */
    @NotBlank(message = "数据集名称不能为空")
    private String name;

    /** 描述 */
    private String description;

    /** 字段列表(全量重建) */
    @Valid
    private List<DatasetFieldCmd> fields;

    /** 修改人(controller 透传) */
    private String updatedBy;
}
