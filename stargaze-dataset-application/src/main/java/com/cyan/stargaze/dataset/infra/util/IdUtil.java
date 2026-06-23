package com.cyan.stargaze.dataset.infra.util;

/**
 * ID 转换工具(DO Long <-> Domain String)。
 * <p>
 * Domain 层 id 统一 String(避免前端 JS Long 精度丢失),DO 层与数据库一致为 Long。
 *
 * @author cy.Y
 * @since 1.0.0
 */
public final class IdUtil {

    private IdUtil() {
    }

    /**
     * String id -> Long(空/非法返回 null)
     */
    public static Long toLong(String id) {
        if (id == null || id.isBlank()) {
            return null;
        }
        try {
            return Long.valueOf(id);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Long id -> String
     */
    public static String toString(Long id) {
        return id == null ? null : String.valueOf(id);
    }
}
