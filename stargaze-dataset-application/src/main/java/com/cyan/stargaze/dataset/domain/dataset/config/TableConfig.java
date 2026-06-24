package com.cyan.stargaze.dataset.domain.dataset.config;

import com.cyan.arch.common.api.Assert;
import com.cyan.arch.common.api.SilentException;
import com.cyan.stargaze.dataset.enums.DatasetSourceType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 数据库表数据集配置。
 *
 * <pre>
 * { "schema": "dwd", "tableName": "dwd_trade_order" }
 * </pre>
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class TableConfig extends DatasetConfig {

    /** 库/schema */
    private String schema;

    /** 表名 */
    private String tableName;

    @Override
    public DatasetSourceType type() {
        return DatasetSourceType.TABLE;
    }

    @Override
    public void validate() {
        Assert.notBlank(this.tableName, new SilentException("数据集配置缺少表名"));
    }
}
