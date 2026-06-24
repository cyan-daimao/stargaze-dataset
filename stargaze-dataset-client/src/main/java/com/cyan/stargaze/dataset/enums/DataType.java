package com.cyan.stargaze.dataset.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 字段数据类型(统一逻辑类型,与具体数据库方言无关)
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum DataType {

    /** 字符串 */
    STRING("string", "字符串"),
    /** 整数 */
    INT("int", "整数"),
    /** 小数 */
    DECIMAL("decimal", "小数"),
    /** 日期 */
    DATE("date", "日期"),
    /** 日期时间 */
    DATETIME("datetime", "日期时间"),
    /** 布尔 */
    BOOLEAN("boolean", "布尔");

    /** 类型编码 */
    @EnumValue
    private final String code;
    /** 展示名称 */
    private final String displayName;

    /**
     * 映射为前端展示用的数据库类型名(BIGINT/VARCHAR 等)。
     */
    public String toDisplayType() {
        return switch (this) {
            case STRING -> "VARCHAR";
            case INT -> "INT";
            case DECIMAL -> "DECIMAL";
            case DATE -> "DATE";
            case DATETIME -> "DATETIME";
            case BOOLEAN -> "BOOLEAN";
        };
    }
}
