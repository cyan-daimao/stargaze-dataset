package com.cyan.stargaze.dataset.domain.dataset.config;

import com.cyan.arch.common.api.Assert;
import com.cyan.arch.common.api.SilentException;
import com.cyan.stargaze.dataset.enums.DatasetSourceType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * SQL 数据集配置。
 *
 * <pre>
 * { "sql": "select id, amount from orders" }
 * </pre>
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class SqlConfig extends DatasetConfig {

    /** 自定义 SQL */
    private String sql;

    /** 库/schema(执行前设置连接 schema,可空) */
    private String schema;

    @Override
    public DatasetSourceType type() {
        return DatasetSourceType.SQL;
    }

    @Override
    public void validate() {
        Assert.notBlank(this.sql, new SilentException("数据集配置缺少 SQL"));
    }
}
