package com.cyan.stargaze.dataset.adapter.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 通用分页响应 DTO。
 *
 * @param <T> 列表元素类型
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class PageDTO<T> {

    /** 列表 */
    private List<T> list;

    /** 总数 */
    private Long total;

    /** 当前页码 */
    private Integer page;

    /** 每页条数 */
    private Integer size;
}
