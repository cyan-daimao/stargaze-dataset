package com.cyan.stargaze.dataset.domain.dataset.valobj;

import com.cyan.stargaze.dataset.enums.DataType;

/**
 * 源库数据类型 -> 统一逻辑类型推断策略(领域值对象)。
 * <p>
 * 供 table/sql/join/excel 字段解析共用;由领域对象或领域方法调用,禁止应用层直接依赖 infra 工具。
 *
 * @author cy.Y
 * @since 1.0.0
 */
public final class DataTypeInferrer {

    private DataTypeInferrer() {
    }

    /**
     * 按源库类型字符串(大小写不敏感,包含匹配)推断逻辑类型。
     *
     * @param sourceType 源库类型字符串
     * @return 推断后的逻辑数据类型
     */
    public static DataType infer(String sourceType) {
        if (sourceType == null) {
            return DataType.STRING;
        }
        String lower = sourceType.toLowerCase();
        if (lower.contains("int") || lower.contains("bigint") || lower.contains("smallint") || lower.contains("tinyint")) {
            return DataType.INT;
        }
        if (lower.contains("decimal") || lower.contains("numeric") || lower.contains("double") || lower.contains("float")) {
            return DataType.DECIMAL;
        }
        if (lower.contains("bool")) {
            return DataType.BOOLEAN;
        }
        if (lower.contains("datetime") || lower.contains("timestamp")) {
            return DataType.DATETIME;
        }
        if (lower.contains("date")) {
            return DataType.DATE;
        }
        return DataType.STRING;
    }
}
