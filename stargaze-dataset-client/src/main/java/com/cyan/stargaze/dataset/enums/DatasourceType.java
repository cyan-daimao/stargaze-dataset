package com.cyan.stargaze.dataset.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据源类型
 * <p>
 * 一期适配:MySQL、PostgreSQL、StarRocks、ClickHouse、Doris、MaxCompute;
 * Excel/CSV、API 为文件/应用类数据源。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum DatasourceType {

    /** MySQL */
    MYSQL("MYSQL", "MySQL"),
    /** PostgreSQL */
    POSTGRESQL("POSTGRESQL", "PostgreSQL"),
    /** StarRocks(OLAP,亦作物化加速目标引擎) */
    STARROCKS("STARROCKS", "StarRocks"),
    /** ClickHouse */
    CLICKHOUSE("CLICKHOUSE", "ClickHouse"),
    /** Apache Doris */
    DORIS("DORIS", "Doris"),
    /** MaxCompute */
    MAXCOMPUTE("MAXCOMPUTE", "MaxCompute"),
    /** Excel 文件 */
    EXCEL("EXCEL", "Excel"),
    /** CSV 文件 */
    CSV("CSV", "CSV"),
    /** RESTful API(JSON) */
    API("API", "API");

    /** 类型编码(入库 type 列) */
    @EnumValue
    private final String code;
    /** 展示名称 */
    private final String displayName;

    public static DatasourceType getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (DatasourceType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        return null;
    }
}
