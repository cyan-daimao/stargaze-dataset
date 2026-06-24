package com.cyan.stargaze.dataset.application.dataset.convert;

import com.cyan.arch.base.mapstruct.MapstructConvert;
import com.cyan.stargaze.dataset.application.dataset.bo.DatasetFileBO;
import com.cyan.stargaze.dataset.domain.dataset.DatasetFile;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 数据集文件 Domain <-> BO 转换。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Mapper(componentModel = "spring", uses = MapstructConvert.class)
public interface DatasetFileAppConvert {

    DatasetFileAppConvert INSTANCE = Mappers.getMapper(DatasetFileAppConvert.class);

    /** Domain -> BO */
    DatasetFileBO toDatasetFileBO(DatasetFile datasetFile);
}
