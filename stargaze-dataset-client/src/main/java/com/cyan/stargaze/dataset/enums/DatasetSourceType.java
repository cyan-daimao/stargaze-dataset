package com.cyan.stargaze.dataset.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据集来源类型
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum DatasetSourceType {

    /** 单表数据集(直接基于一张表) */
    TABLE("TABLE", "单表"),
    /** SQL 数据集(自定义 SQL) */
    SQL("SQL", "SQL"),
    /** 多表关联(可视化 JOIN) */
    JOIN("JOIN", "多表关联"),
    /** Excel 数据集(上传文件入库) */
    EXCEL("EXCEL", "Excel"),
    /** 数据集组合(UNION/JOIN,二期) */
    UNION("UNION", "数据集组合");

    /** 类型编码 */
    @EnumValue
    private final String code;
    /** 展示名称 */
    private final String displayName;
}
