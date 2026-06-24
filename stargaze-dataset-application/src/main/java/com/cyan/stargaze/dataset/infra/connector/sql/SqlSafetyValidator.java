package com.cyan.stargaze.dataset.infra.connector.sql;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * SQL 安全校验器:仅允许单条只读 SELECT / WITH ... SELECT。
 * <p>
 * 校验流程(顺序执行,任一失败即拒绝):
 * <ol>
 *   <li>非空与长度上限</li>
 *   <li>判定用副本:去块注释(普通 /* *\/,但保留 MySQL 条件执行注释 /*! *\/)/行注释/字符串字面量</li>
 *   <li>去尾分号,禁止中部分号(防多语句)</li>
 *   <li>必须以 select / with 开头</li>
 *   <li>语句关键字黑名单(带语句上下文,避免误杀 REPLACE()/INSERT() 等同名函数)</li>
 *   <li>into outfile / dumpfile 拦截</li>
 * </ol>
 * 返回 sanitized = 去尾分号后的原 SQL(保留合法注释,供执行)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Component
public class SqlSafetyValidator {

    private static final int MAX_LENGTH = 65536;

    /** 普通块注释 /* ... *\/,但不匹配 MySQL 条件执行注释 /*! ... *\/(后者会被 MySQL 执行,必须保留以纳入判定) */
    private static final Pattern BLOCK_COMMENT = Pattern.compile("/\\*(?!!).*?\\*/", Pattern.DOTALL);

    /** 行注释 -- 与 # */
    private static final Pattern LINE_COMMENT = Pattern.compile("(--|#)[^\\n]*");

    /** 单引号字符串字面量(转义 '' ) */
    private static final Pattern SINGLE_QUOTED = Pattern.compile("'(?:[^']|'')*'");

    /** 双引号字符串字面量(转义 "" ) */
    private static final Pattern DOUBLE_QUOTED = Pattern.compile("\"(?:[^\"]|\"\")*\"");

    /** 仅允许以 select / with 开头(允许前置括号与空白) */
    private static final Pattern READ_ONLY_START = Pattern.compile("^\\s*\\(?\\s*(select|with)\\b", Pattern.CASE_INSENSITIVE);

    /** 危险语句关键字(带语句上下文,避免误杀 REPLACE()/INSERT() 等同名字符串函数) */
    private static final Pattern BLACKLIST = Pattern.compile(
            "\\b(insert\\s+into|update\\s+\\w|delete\\s+from|drop\\s+(table|database|schema|index|view)|"
                    + "truncate\\s+table|alter\\s+\\w|create\\s+(table|database|schema|index|view|user)|"
                    + "grant\\s+|revoke\\s+|call\\s+\\w|merge\\s+into|rename\\s+\\w|"
                    + "lock\\s+tables|unlock\\s+tables|use\\s+\\w|load\\s+data)\\b",
            Pattern.CASE_INSENSITIVE);

    /** 写文件拦截 */
    private static final Pattern INTO_OUTFILE = Pattern.compile("into\\s+(outfile|dumpfile)\\b", Pattern.CASE_INSENSITIVE);

    /**
     * 校验 SQL 是否安全可执行。
     */
    public SqlSafetyResult validate(String sql) {
        if (sql == null || sql.isBlank()) {
            return SqlSafetyResult.fail("SQL 不能为空");
        }
        if (sql.length() > MAX_LENGTH) {
            return SqlSafetyResult.fail("SQL 长度超过上限 " + MAX_LENGTH);
        }
        // 判定用副本:去注释与字面量,避免被其内容干扰关键字判定
        // (条件执行注释 /*! 保留,因其会被 MySQL 执行,需纳入判定)
        String stripped = BLOCK_COMMENT.matcher(sql).replaceAll(" ");
        stripped = LINE_COMMENT.matcher(stripped).replaceAll(" ");
        stripped = SINGLE_QUOTED.matcher(stripped).replaceAll("?");
        stripped = DOUBLE_QUOTED.matcher(stripped).replaceAll("?");

        String trimmed = stripped.trim();
        if (trimmed.isEmpty()) {
            return SqlSafetyResult.fail("SQL 不能为空");
        }

        // 去尾分号;若仍含分号则为多语句
        if (trimmed.endsWith(";")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1).trim();
        }
        if (trimmed.contains(";")) {
            return SqlSafetyResult.fail("禁止多语句分号拼接");
        }

        if (!READ_ONLY_START.matcher(trimmed).find()) {
            return SqlSafetyResult.fail("仅允许 SELECT 或 WITH ... SELECT 语句");
        }

        java.util.regex.Matcher black = BLACKLIST.matcher(trimmed);
        if (black.find()) {
            return SqlSafetyResult.fail("禁止的语句关键字: " + black.group(1));
        }

        if (INTO_OUTFILE.matcher(trimmed).find()) {
            return SqlSafetyResult.fail("禁止导出到文件");
        }

        // sanitized:去尾分号后的原 SQL(保留合法注释,供执行拼接)
        String sanitized = sql.trim();
        while (sanitized.endsWith(";")) {
            sanitized = sanitized.substring(0, sanitized.length() - 1).trim();
        }
        return SqlSafetyResult.ok(sanitized);
    }
}
