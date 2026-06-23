package com.cyan.stargaze.dataset.adapter.datasource.http.convert;

import com.cyan.arch.base.mapstruct.MapstructConvert;
import com.cyan.stargaze.dataset.adapter.datasource.http.dto.ColumnDTO;
import com.cyan.stargaze.dataset.adapter.datasource.http.dto.DatabaseDTO;
import com.cyan.stargaze.dataset.adapter.datasource.http.dto.DatasourceDTO;
import com.cyan.stargaze.dataset.adapter.datasource.http.dto.TableSampleDTO;
import com.cyan.stargaze.dataset.adapter.datasource.http.dto.TableSchemaDTO;
import com.cyan.stargaze.dataset.application.datasource.bo.DatasourceBO;
import com.cyan.stargaze.dataset.domain.datasource.valobj.ColumnValObj;
import com.cyan.stargaze.dataset.domain.datasource.valobj.DatabaseValObj;
import com.cyan.stargaze.dataset.domain.datasource.valobj.TableSampleValObj;
import com.cyan.stargaze.dataset.domain.datasource.valobj.TableSchemaValObj;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 数据源适配层转换(BO -> DTO,密码脱敏)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Mapper(componentModel = "spring", uses = MapstructConvert.class)
public interface DatasourceAdapterConvert {

    DatasourceAdapterConvert INSTANCE = Mappers.getMapper(DatasourceAdapterConvert.class);

    /**
     * BO -> DTO(密码脱敏)
     */
    @Mapping(target = "config", source = "config", qualifiedByName = "maskConfig")
    DatasourceDTO toDatasourceDTO(DatasourceBO bo);

    DatabaseDTO toDatabaseDTO(DatabaseValObj valObj);

    TableSchemaDTO toTableSchemaDTO(TableSchemaValObj valObj);

    ColumnDTO toColumnDTO(ColumnValObj valObj);

    List<ColumnDTO> toColumnDTOList(List<ColumnValObj> valObjs);

    TableSampleDTO toTableSampleDTO(TableSampleValObj valObj);

    /**
     * 密码脱敏:返回前清除明文密码
     */
    @org.mapstruct.Named("maskConfig")
    default com.cyan.stargaze.dataset.domain.datasource.valobj.DataSourceConfig maskConfig(
            com.cyan.stargaze.dataset.domain.datasource.valobj.DataSourceConfig config) {
        if (config == null) {
            return null;
        }
        config.setPassword(null);
        return config;
    }
}
