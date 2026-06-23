package com.cyan.stargaze.dataset.infra.persistence.dataset.convert;

import com.cyan.arch.base.mapstruct.MapstructConvert;
import com.cyan.stargaze.dataset.domain.dataset.MaterializedView;
import com.cyan.stargaze.dataset.infra.persistence.dataset.dos.MaterializedViewDO;
import com.cyan.stargaze.dataset.infra.util.IdUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 物化加速配置 DO <-> Domain 转换。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Mapper(componentModel = "spring", uses = MapstructConvert.class)
public abstract class MaterializedViewInfraConvert {

    public static final MaterializedViewInfraConvert INSTANCE = Mappers.getMapper(MaterializedViewInfraConvert.class);

    @Mapping(target = "id", source = "id", qualifiedByName = "longToString")
    @Mapping(target = "datasetId", source = "datasetId", qualifiedByName = "longToString")
    @Mapping(target = "createdBy", source = "createdBy", qualifiedByName = "longToString")
    @Mapping(target = "updatedBy", source = "updatedBy", qualifiedByName = "longToString")
    public abstract MaterializedView toMaterializedView(MaterializedViewDO viewDO);

    public abstract List<MaterializedView> toMaterializedViewList(List<MaterializedViewDO> viewDOs);

    @Mapping(target = "id", source = "id", qualifiedByName = "stringToLong")
    @Mapping(target = "datasetId", source = "datasetId", qualifiedByName = "stringToLong")
    @Mapping(target = "createdBy", source = "createdBy", qualifiedByName = "stringToLong")
    @Mapping(target = "updatedBy", source = "updatedBy", qualifiedByName = "stringToLong")
    public abstract MaterializedViewDO toMaterializedViewDO(MaterializedView view);

    @Named("longToString")
    protected String longToString(Long value) {
        return IdUtil.toString(value);
    }

    @Named("stringToLong")
    protected Long stringToLong(String value) {
        return IdUtil.toLong(value);
    }
}
