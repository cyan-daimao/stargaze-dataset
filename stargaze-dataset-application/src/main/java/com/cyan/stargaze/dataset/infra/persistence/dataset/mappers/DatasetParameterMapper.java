package com.cyan.stargaze.dataset.infra.persistence.dataset.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cyan.stargaze.dataset.infra.persistence.dataset.dos.DatasetParameterDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据集参数字段 Mapper。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Mapper
public interface DatasetParameterMapper extends BaseMapper<DatasetParameterDO> {
}
