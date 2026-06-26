package com.cyan.stargaze.dataset.infra.persistence.datasetfile.convert;

import com.cyan.arch.base.mapstruct.MapstructConvert;
import com.cyan.stargaze.dataset.domain.dataset.DatasetFile;
import com.cyan.stargaze.dataset.infra.persistence.datasetfile.dos.DatasetFileDO;
import com.cyan.stargaze.dataset.infra.util.IdUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

/**
 * 数据集文件 DO <-> Domain 转换(Long <-> String via IdUtil)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Mapper(componentModel = "spring", uses = MapstructConvert.class)
public abstract class DatasetFileInfraConvert {

    public static final DatasetFileInfraConvert INSTANCE = Mappers.getMapper(DatasetFileInfraConvert.class);

    /** DO -> Domain */
    @Mapping(target = "id", source = "id", qualifiedByName = "longToString")
    @Mapping(target = "createdBy", source = "createdBy", qualifiedByName = "longToString")
    @Mapping(target = "updatedBy", source = "updatedBy", qualifiedByName = "longToString")
    public abstract DatasetFile toDatasetFile(DatasetFileDO datasetFileDO);

    /** Domain -> DO */
    @Mapping(target = "id", source = "id", qualifiedByName = "stringToLong")
    @Mapping(target = "createdBy", source = "createdBy", qualifiedByName = "stringToLong")
    @Mapping(target = "updatedBy", source = "updatedBy", qualifiedByName = "stringToLong")
    public abstract DatasetFileDO toDatasetFileDO(DatasetFile datasetFile);

    @Named("longToString")
    protected String longToString(Long value) {
        return IdUtil.toString(value);
    }

    @Named("stringToLong")
    protected Long stringToLong(String value) {
        return IdUtil.toLong(value);
    }
}
