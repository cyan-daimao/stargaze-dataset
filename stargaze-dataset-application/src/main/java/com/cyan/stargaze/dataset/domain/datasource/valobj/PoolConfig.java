package com.cyan.stargaze.dataset.domain.datasource.valobj;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 数据源连接池配置值对象。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class PoolConfig {

    /** 最大连接数 */
    private Integer maxSize;

    /** 最小空闲连接 */
    private Integer minIdle;

    /** 连接超时(毫秒) */
    private Integer timeout;
}
