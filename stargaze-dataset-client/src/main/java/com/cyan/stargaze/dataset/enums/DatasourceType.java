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
    MYSQL("mysql", "MySQL"),
    /** PostgreSQL */
    POSTGRESQL("postgresql", "PostgreSQL"),
    /** StarRocks(OLAP,亦作物化加速目标引擎) */
    STARROCKS("starrocks", "StarRocks"),
    /** ClickHouse */
    CLICKHOUSE("clickhouse", "ClickHouse"),
    /** Apache Doris */
    DORIS("doris", "Doris"),
    /** MaxCompute */
    MAXCOMPUTE("maxcompute", "MaxCompute"),
    /** Excel 文件 */
    EXCEL("excel", "Excel"),
    /** CSV 文件 */
    CSV("csv", "CSV"),
    /** RESTful API(JSON) */
    API("api", "API");

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
