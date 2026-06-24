package com.cyan.stargaze.dataset.adapter.dataset.http.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 表列表 DTO(schemas + tables)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class TableListDTO {

    /** 该数据源是否支持独立的 schema 层(false 表示连接已指向具体库,tables 直接返回该库下所有表) */
    private Boolean schemaSupported;
    private List<String> schemas;
    private List<TableMetaDTO> tables;
}
