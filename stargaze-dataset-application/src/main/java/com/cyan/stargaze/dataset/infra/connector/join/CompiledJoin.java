package com.cyan.stargaze.dataset.infra.connector.join;

import com.cyan.stargaze.dataset.domain.datasource.valobj.ColumnValObj;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * JOIN 编译结果:可执行 SQL + 字段列表(originName 已按 表别名.列名 去重)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class CompiledJoin {

    /** 编译后的可执行 SQL */
    private String sql;

    /** 字段列表(name 为 originName,可能为 表别名.列名) */
    private List<ColumnValObj> columns;
}
