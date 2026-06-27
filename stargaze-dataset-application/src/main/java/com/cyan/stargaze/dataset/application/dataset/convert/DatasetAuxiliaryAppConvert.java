package com.cyan.stargaze.dataset.application.dataset.convert;

import com.cyan.arch.base.mapstruct.MapstructConvert;
import com.cyan.stargaze.dataset.application.dataset.bo.DatasetHierarchyBO;
import com.cyan.stargaze.dataset.application.dataset.bo.DatasetParameterBO;
import com.cyan.stargaze.dataset.application.dataset.bo.MaterializedViewBO;
import com.cyan.stargaze.dataset.application.dataset.cmd.DatasetHierarchyCmd;
import com.cyan.stargaze.dataset.application.dataset.cmd.DatasetParameterCmd;
import com.cyan.stargaze.dataset.application.dataset.cmd.MaterializedViewCmd;
import com.cyan.stargaze.dataset.domain.dataset.DatasetHierarchy;
import com.cyan.stargaze.dataset.domain.dataset.DatasetParameter;
import com.cyan.stargaze.dataset.domain.dataset.MaterializedView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 数据集辅助对象(层级/参数/物化)应用层转换(Cmd -> Domain)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Mapper(componentModel = "spring", uses = MapstructConvert.class)
public interface DatasetAuxiliaryAppConvert {

    DatasetAuxiliaryAppConvert INSTANCE = Mappers.getMapper(DatasetAuxiliaryAppConvert.class);

    /** Cmd -> 维度层级 Domain */
    @Mapping(target = "save", ignore = true)
    @Mapping(target = "update", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    DatasetHierarchy toHierarchy(DatasetHierarchyCmd cmd);

    /** Cmd -> 参数字段 Domain */
    @Mapping(target = "save", ignore = true)
    @Mapping(target = "update", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    DatasetParameter toParameter(DatasetParameterCmd cmd);

    /** Cmd -> 物化加速配置 Domain */
    @Mapping(target = "save", ignore = true)
    @Mapping(target = "update", ignore = true)
    @Mapping(target = "lastSyncAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    MaterializedView toMaterializedView(MaterializedViewCmd cmd);

    /** 维度层级 Domain -> BO */
    DatasetHierarchyBO toHierarchyBO(DatasetHierarchy hierarchy);

    List<DatasetHierarchyBO> toHierarchyBOList(List<DatasetHierarchy> hierarchies);

    /** 参数字段 Domain -> BO */
    DatasetParameterBO toParameterBO(DatasetParameter parameter);

    List<DatasetParameterBO> toParameterBOList(List<DatasetParameter> parameters);

    /** 物化加速配置 Domain -> BO */
    MaterializedViewBO toMaterializedViewBO(MaterializedView view);

    List<MaterializedViewBO> toMaterializedViewBOList(List<MaterializedView> views);
}
