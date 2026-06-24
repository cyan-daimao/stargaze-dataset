package com.cyan.stargaze.dataset.adapter.dataset.http.dto;

import com.cyan.stargaze.dataset.domain.dataset.config.DatasetConfig;
import com.cyan.stargaze.dataset.enums.DatasetSourceType;
import com.cyan.stargaze.dataset.enums.DatasetStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * 数据集详情 DTO(含 config/fields/statistics)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class DatasetDetailDTO {

    private String id;
    private String name;
    private String description;
    private DatasetSourceType sourceType;
    private String sourceTypeName;
    private String datasourceId;
    private String datasourceName;
    private DatasetStatus status;
    private String version;
    private String creator;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private OffsetDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private OffsetDateTime updatedAt;

    /** 来源配置(按类型) */
    private DatasetConfig config;

    /** 字段列表 */
    private List<DatasetFieldDTO> fields;

    /** 统计信息 */
    private DatasetStatisticsDTO statistics;
}
