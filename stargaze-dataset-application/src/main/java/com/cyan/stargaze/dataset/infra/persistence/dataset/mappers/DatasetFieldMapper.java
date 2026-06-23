package com.cyan.stargaze.dataset.infra.persistence.dataset.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cyan.stargaze.dataset.infra.persistence.dataset.dos.DatasetFieldDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据集字段 Mapper。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Mapper
public interface DatasetFieldMapper extends BaseMapper<DatasetFieldDO> {
}
