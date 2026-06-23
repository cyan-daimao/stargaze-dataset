package com.cyan.stargaze.dataset.infra.persistence.dataset.convert;

import com.cyan.arch.base.mapstruct.MapstructConvert;
import com.cyan.stargaze.dataset.domain.dataset.DatasetHierarchy;
import com.cyan.stargaze.dataset.infra.persistence.dataset.dos.DatasetHierarchyDO;
import com.cyan.stargaze.dataset.infra.util.IdUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 数据集维度层级 DO <-> Domain 转换。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Mapper(componentModel = "spring", uses = MapstructConvert.class)
public abstract class DatasetHierarchyInfraConvert {

    public static final DatasetHierarchyInfraConvert INSTANCE = Mappers.getMapper(DatasetHierarchyInfraConvert.class);

    @Mapping(target = "id", source = "id", qualifiedByName = "longToString")
    @Mapping(target = "datasetId", source = "datasetId", qualifiedByName = "longToString")
    public abstract DatasetHierarchy toHierarchy(DatasetHierarchyDO hierarchyDO);

    public abstract List<DatasetHierarchy> toHierarchyList(List<DatasetHierarchyDO> hierarchyDOs);

    @Mapping(target = "id", source = "id", qualifiedByName = "stringToLong")
    @Mapping(target = "datasetId", source = "datasetId", qualifiedByName = "stringToLong")
    public abstract DatasetHierarchyDO toHierarchyDO(DatasetHierarchy hierarchy);

    @Named("longToString")
    protected String longToString(Long value) {
        return IdUtil.toString(value);
    }

    @Named("stringToLong")
    protected Long stringToLong(String value) {
        return IdUtil.toLong(value);
    }
}
