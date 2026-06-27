package com.cyan.stargaze.dataset.infra.persistence.dataset.convert;

import com.cyan.arch.base.mapstruct.MapstructConvert;
import com.cyan.stargaze.dataset.domain.dataset.Dataset;
import com.cyan.stargaze.dataset.infra.persistence.dataset.dos.DatasetDO;
import com.cyan.stargaze.dataset.infra.util.IdUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

/**
 * 数据集主表 DO <-> Domain 转换(不含字段明细,字段单独转换)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Mapper(componentModel = "spring", uses = MapstructConvert.class)
public abstract class DatasetInfraConvert {

    public static final DatasetInfraConvert INSTANCE = Mappers.getMapper(DatasetInfraConvert.class);

    @Mapping(target = "id", source = "id", qualifiedByName = "longToString")
    @Mapping(target = "dataSourceId", source = "dataSourceId", qualifiedByName = "longToString")
    @Mapping(target = "createdBy", source = "createdBy", qualifiedByName = "longToString")
    @Mapping(target = "updatedBy", source = "updatedBy", qualifiedByName = "longToString")
    @Mapping(target = "fields", ignore = true)
    @Mapping(target = "save", ignore = true)
    @Mapping(target = "update", ignore = true)
    public abstract Dataset toDataset(DatasetDO datasetDO);

    @Mapping(target = "id", source = "id", qualifiedByName = "stringToLong")
    @Mapping(target = "dataSourceId", source = "dataSourceId", qualifiedByName = "stringToLong")
    @Mapping(target = "createdBy", source = "createdBy", qualifiedByName = "stringToLong")
    @Mapping(target = "updatedBy", source = "updatedBy", qualifiedByName = "stringToLong")
    public abstract DatasetDO toDatasetDO(Dataset dataset);

    @Named("longToString")
    protected String longToString(Long value) {
        return IdUtil.toString(value);
    }

    @Named("stringToLong")
    protected Long stringToLong(String value) {
        return IdUtil.toLong(value);
    }
}
