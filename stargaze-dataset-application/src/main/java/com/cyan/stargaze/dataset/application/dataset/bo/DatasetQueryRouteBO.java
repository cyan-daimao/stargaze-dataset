package com.cyan.stargaze.dataset.application.dataset.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * 数据集查询路由业务对象。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class DatasetQueryRouteBO {

    /** 数据集 ID */
    private String datasetId;

    /** 执行模式:MATERIALIZED/CATALOG/EXCEL_TABLE */
    private String executionMode;

    /** 执行引擎 */
    private String engine;

    /** 完整表引用 */
    private String tableRef;

    /** StarRocks catalog 名称 */
    private String catalogName;

    /** StarRocks database 名称 */
    private String databaseName;

    /** 源端 schema 名称 */
    private String schemaName;

    /** StarRocks 或源端表名 */
    private String tableName;

    /** 同步状态 */
    private String syncStatus;

    /** 最近同步时间 */
    private OffsetDateTime lastSyncAt;

    /** 最近同步错误 */
    private String lastError;

    /** 字段映射 */
    private Map<String, String> fieldMappings;
}
