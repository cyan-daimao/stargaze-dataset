package com.cyan.stargaze.dataset.infra.persistence.datasource.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cyan.stargaze.dataset.infra.persistence.datasource.dos.DataSourceDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据源 Mapper。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Mapper
public interface DataSourceMapper extends BaseMapper<DataSourceDO> {
}
