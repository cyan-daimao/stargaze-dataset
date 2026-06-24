package com.cyan.stargaze.dataset.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * JOIN 数据集关联类型(config 内使用,不入库无需 @EnumValue)。
 * <p>
 * 枚举名与前端契约一致(LEFT_JOIN 等),fastjson2 按枚举名反序列化;
 * {@link #sql()} 返回 SQL 片段(left join 等)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum JoinType {

    /** 内连接 */
    INNER_JOIN("inner"),
    /** 左连接 */
    LEFT_JOIN("left"),
    /** 右连接 */
    RIGHT_JOIN("right"),
    /** 全连接 */
    FULL_JOIN("full");

    /** SQL 关键字编码 */
    private final String code;

    /**
     * 返回 SQL 片段,如 "left join"
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
