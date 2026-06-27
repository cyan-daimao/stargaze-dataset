package com.cyan.stargaze.dataset.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * JOIN 数据集关联类型(config 内使用,不入库无需 @EnumValue)。
 * <p>
 * JSON 序列化使用 camelCase(如 leftJoin),反序列化兼容旧格式。
 * {@link #sql()} 返回 SQL 片段(left join 等)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum JoinType {

    /** 内连接 */
    INNER_JOIN("INNER"),
    /** 左连接 */
    LEFT_JOIN("LEFT"),
    /** 右连接 */
    RIGHT_JOIN("RIGHT"),
    /** 全连接 */
    FULL_JOIN("FULL");

    /** SQL 关键字编码 */
    private final String code;

    /**
     * 返回 JSON 驼峰值,如 "leftJoin"。
     */
    @JsonValue
    public String toJsonValue() {
        return name().charAt(0) + name().substring(1).toLowerCase().replace("_", "");
    }

    /**
     * 从 JSON 反序列化,兼容旧 UPPER_SNAKE_CASE 与新 camelCase。
     */
    @JsonCreator
    public static JoinType fromJson(String value) {
        if (value == null) {
            return null;
        }
        // 先尝试直接匹配枚举名
        for (JoinType type : values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        // 再尝试按 code 匹配
        for (JoinType type : values()) {
            if (type.code.equalsIgnoreCase(value)) {
                return type;
            }
        }
        // 最后尝试 camelCase 匹配(去掉下划线比较)
        String normalized = value.replace("_", "");
        for (JoinType type : values()) {
            if (type.name().replace("_", "").equalsIgnoreCase(normalized)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 返回 SQL 片段,如 "LEFT join"
     */
    public String sql() {
        return code + " join";
    }

    /**
     * 按编码解析,未匹配返回 null
     */
    public static JoinType getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (JoinType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        return null;
    }
}
