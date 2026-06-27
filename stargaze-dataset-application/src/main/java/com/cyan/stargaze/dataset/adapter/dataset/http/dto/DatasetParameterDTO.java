package com.cyan.stargaze.dataset.adapter.dataset.http.dto;

import com.cyan.stargaze.dataset.enums.DataType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;

/**
 * 数据集参数字段返回 DTO。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class DatasetParameterDTO {

    /** 主键 */
    private String id;

    /** 所属数据集 ID */
    private String datasetId;

    /** 参数名(数据集内唯一) */
    private String name;

    /** 参数别名 */
    private String alias;

    /** 数据类型 */
    private DataType dataType;

    /** 默认值 */
    private String defaultValue;

    /** 创建时间 */
    private OffsetDateTime createdAt;

    /** 更新时间 */
    private OffsetDateTime updatedAt;
}
