package com.cyan.stargaze.dataset.domain.dataset.config;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 表引用(schema + 表名)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
public class TableRef {

    /** 库/schema */
    private String schema;

    /** 表名 */
    private String tableName;
}
