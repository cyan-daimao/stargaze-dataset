package com.cyan.stargaze.dataset.domain.dataset.config;

import com.cyan.stargaze.dataset.enums.DatasetSourceType;

/**
 * 数据集来源配置抽象基类。
 * <p>
 * 对应 dataset.definition 列(jsonb 序列化字符串),按 {@link DatasetSourceType} 分化为
 * table/sql/join/excel 四类配置,提供类型安全的解析与校验。
 *
 * @author cy.Y
 * @since 1.0.0
 */
public abstract class DatasetConfig {

    /**
     * 来源类型
     */
    public abstract DatasetSourceType type();

    /**
     * 结构校验(必填项)
     */
    public abstract void validate();
}
