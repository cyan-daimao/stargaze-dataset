package com.cyan.stargaze.dataset.infra.persistence.dataset.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cyan.stargaze.dataset.infra.persistence.dataset.dos.DatasetHierarchyDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据集维度层级 Mapper。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Mapper
public interface DatasetHierarchyMapper extends BaseMapper<DatasetHierarchyDO> {
}
