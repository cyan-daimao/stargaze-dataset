package com.cyan.stargaze.dataset.domain.dataset.config;

import com.cyan.arch.common.api.Assert;
import com.cyan.arch.common.api.SilentException;
import com.cyan.stargaze.dataset.enums.DatasetSourceType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 多表关联数据集配置。
 *
 * <pre>
 * {
 *   "mainTable": { "schema": "dwd", "tableName": "dwd_trade_order" },
 *   "joins": [
 *     { "joinType": "LEFT_JOIN", "table": { "schema": "dim", "tableName": "dim_product" },
 *       "conditions": [ { "leftField": "product_id", "rightField": "product_id", "operator": "=" } ] }
 *   ]
 * }
 * </pre>
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class JoinConfig extends DatasetConfig {

    /** 主表 */
    private TableRef mainTable;

    /** 关联表列表 */
    private List<JoinClause> joins;

    @Override
    public DatasetSourceType type() {
        return DatasetSourceType.JOIN;
    }

    @Override
    public void validate() {
        Assert.notNull(this.mainTable, new SilentException("JOIN 数据集配置缺少主表"));
        Assert.notBlank(this.mainTable.getTableName(), new SilentException("JOIN 数据集主表缺少表名"));
        Assert.notEmpty(this.joins, new SilentException("JOIN 数据集配置缺少关联表"));
        for (JoinClause clause : joins) {
            Assert.notNull(clause.getJoinType(), new SilentException("JOIN 类型不能为空"));
            Assert.notNull(clause.getTable(), new SilentException("JOIN 关联表不能为空"));
            Assert.notBlank(clause.getTable().getTableName(), new SilentException("JOIN 关联表缺少表名"));
            Assert.notEmpty(clause.getConditions(), new SilentException("JOIN 缺少关联条件: " + clause.getTable().getTableName()));
        }
    }
}
