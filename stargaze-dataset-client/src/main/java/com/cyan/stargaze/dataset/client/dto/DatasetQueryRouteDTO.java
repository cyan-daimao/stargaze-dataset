package com.cyan.stargaze.dataset.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * 数据集查询路由 DTO。
 * <p>
 * query 服务按 datasetId 调用本契约解析最终 StarRocks 执行表。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class DatasetQueryRouteDTO {

    /** 数据集 ID */
    private String datasetId;

    /** 执行模式:MATERIALIZED/CATALOG/EXCEL_TABLE */
    private String executionMode;

    /** 执行引擎 */
    private String engine;

    /** 完整表引用,如 db.table 或 catalog.db.table */
    private String tableRef;

    /** StarRocks catalog 名称 */
    private String catalogName;

    /** StarRocks database 名称 */
    private String databaseName;

    /** 源端 schema 名称 */
    private String schemaName;

    /** StarRocks 或源端表名 */
    private String tableName;

    /** 同步状态:IDLE/SYNCING/SUCCESS/ERROR */
    private String syncStatus;

    /** 最近同步时间 */
    private OffsetDateTime lastSyncAt;

    /** 最近同步错误 */
    private String lastError;

    /** 字段映射:逻辑字段名 -> 物理字段名 */
    private Map<String, String> fieldMappings;
}
