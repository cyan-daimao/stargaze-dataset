package com.cyan.stargaze.dataset.infra.persistence.dataset.convert;

import com.cyan.arch.base.mapstruct.MapstructConvert;
import com.cyan.stargaze.dataset.domain.dataset.DatasetParameter;
import com.cyan.stargaze.dataset.infra.persistence.dataset.dos.DatasetParameterDO;
import com.cyan.stargaze.dataset.infra.util.IdUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 数据集参数字段 DO <-> Domain 转换。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Mapper(componentModel = "spring", uses = MapstructConvert.class)
public abstract class DatasetParameterInfraConvert {

    public static final DatasetParameterInfraConvert INSTANCE = Mappers.getMapper(DatasetParameterInfraConvert.class);

    @Mapping(target = "id", source = "id", qualifiedByName = "longToString")
    @Mapping(target = "datasetId", source = "datasetId", qualifiedByName = "longToString")
    public abstract DatasetParameter toParameter(DatasetParameterDO parameterDO);

    public abstract List<DatasetParameter> toParameterList(List<DatasetParameterDO> parameterDOs);

    @Mapping(target = "id", source = "id", qualifiedByName = "stringToLong")
    @Mapping(target = "datasetId", source = "datasetId", qualifiedByName = "stringToLong")
    public abstract DatasetParameterDO toParameterDO(DatasetParameter parameter);

    @Named("longToString")
    protected String longToString(Long value) {
        return IdUtil.toString(value);
    }

    @Named("stringToLong")
    protected Long stringToLong(String value) {
        return IdUtil.toLong(value);
    }
}
