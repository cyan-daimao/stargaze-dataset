package com.cyan.stargaze.dataset.infra.persistence.dataset.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cyan.stargaze.dataset.infra.persistence.dataset.dos.MaterializedViewDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 物化加速配置 Mapper。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Mapper
public interface MaterializedViewMapper extends BaseMapper<MaterializedViewDO> {
}
