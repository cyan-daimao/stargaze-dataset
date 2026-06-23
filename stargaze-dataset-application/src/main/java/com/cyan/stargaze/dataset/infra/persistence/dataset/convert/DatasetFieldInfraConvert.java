package com.cyan.stargaze.dataset.infra.persistence.dataset.convert;

import com.cyan.arch.base.mapstruct.MapstructConvert;
import com.cyan.stargaze.dataset.domain.dataset.DatasetField;
import com.cyan.stargaze.dataset.infra.persistence.dataset.dos.DatasetFieldDO;
import com.cyan.stargaze.dataset.infra.util.IdUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 数据集字段 DO <-> Domain 转换。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Mapper(componentModel = "spring", uses = MapstructConvert.class)
public abstract class DatasetFieldInfraConvert {

    public static final DatasetFieldInfraConvert INSTANCE = Mappers.getMapper(DatasetFieldInfraConvert.class);

    @Mapping(target = "id", source = "id", qualifiedByName = "longToString")
    @Mapping(target = "datasetId", source = "datasetId", qualifiedByName = "longToString")
    @Mapping(target = "dictionaryId", source = "dictionaryId", qualifiedByName = "longToString")
    public abstract DatasetField toDatasetField(DatasetFieldDO fieldDO);

    public abstract List<DatasetField> toDatasetFieldList(List<DatasetFieldDO> fieldDOs);

    @Mapping(target = "id", source = "id", qualifiedByName = "stringToLong")
    @Mapping(target = "datasetId", source = "datasetId", qualifiedByName = "stringToLong")
    @Mapping(target = "dictionaryId", source = "dictionaryId", qualifiedByName = "stringToLong")
    public abstract DatasetFieldDO toDatasetFieldDO(DatasetField field);

    public abstract List<DatasetFieldDO> toDatasetFieldDOList(List<DatasetField> fields);

    @Named("longToString")
    protected String longToString(Long value) {
        return IdUtil.toString(value);
    }

    @Named("stringToLong")
    protected Long stringToLong(String value) {
        return IdUtil.toLong(value);
    }
}
