package com.cyan.stargaze.dataset.infra.persistence.datasetfile.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cyan.stargaze.dataset.infra.persistence.datasetfile.dos.DatasetFileDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据集文件 Mapper。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Mapper
public interface DatasetFileMapper extends BaseMapper<DatasetFileDO> {
}
