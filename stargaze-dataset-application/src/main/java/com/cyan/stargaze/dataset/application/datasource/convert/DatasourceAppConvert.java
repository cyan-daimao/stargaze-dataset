package com.cyan.stargaze.dataset.application.datasource.convert;

import com.cyan.arch.base.mapstruct.MapstructConvert;
import com.cyan.stargaze.dataset.application.datasource.bo.DatasourceBO;
import com.cyan.stargaze.dataset.application.datasource.cmd.DatasourceCmd;
import com.cyan.stargaze.dataset.domain.datasource.DataSource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * 数据源应用层转换(Cmd -> Domain, Domain -> BO)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Mapper(componentModel = "spring", uses = MapstructConvert.class)
public interface DatasourceAppConvert {

    DatasourceAppConvert INSTANCE = Mappers.getMapper(DatasourceAppConvert.class);

    /** Domain -> BO */
    DatasourceBO toDatasourceBO(DataSource dataSource);

    /** Cmd -> Domain */
    @Mapping(target = "save", ignore = true)
    @Mapping(target = "update", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    DataSource toDataSource(DatasourceCmd cmd);
}
