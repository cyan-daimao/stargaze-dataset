package com.cyan.stargaze.dataset.infra.util;

import com.cyan.stargaze.dataset.enums.DataType;

/**
 * 源库数据类型 -> 统一逻辑类型推断工具。
 * <p>
 * 供 table/sql/join 字段解析共用。
 *
 * @author cy.Y
 * @since 1.0.0
 */
public final class DataTypeInferrer {

    private DataTypeInferrer() {
    }

    /**
     * 按源库类型字符串(大小写不敏感,包含匹配)推断逻辑类型。
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
