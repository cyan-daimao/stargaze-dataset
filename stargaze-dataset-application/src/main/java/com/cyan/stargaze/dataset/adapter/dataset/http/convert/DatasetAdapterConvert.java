package com.cyan.stargaze.dataset.adapter.dataset.http.convert;

import com.cyan.arch.base.mapstruct.MapstructConvert;
import com.cyan.stargaze.dataset.adapter.dataset.http.dto.DatasetCreateDTO;
import com.cyan.stargaze.dataset.adapter.dataset.http.dto.DatasetDeleteDTO;
import com.cyan.stargaze.dataset.adapter.dataset.http.dto.DatasetDetailDTO;
import com.cyan.stargaze.dataset.adapter.dataset.http.dto.DatasetFieldDTO;
import com.cyan.stargaze.dataset.adapter.dataset.http.dto.DatasetListDTO;
import com.cyan.stargaze.dataset.adapter.dataset.http.dto.DatasetStatisticsDTO;
import com.cyan.stargaze.dataset.adapter.dataset.http.dto.DatasetSyncDTO;
import com.cyan.stargaze.dataset.adapter.dataset.http.dto.DatasetSyncStatusDTO;
import com.cyan.stargaze.dataset.adapter.dataset.http.dto.DatasetUpdateDTO;
import com.cyan.stargaze.dataset.adapter.dataset.http.dto.ExcelColumnDTO;
import com.cyan.stargaze.dataset.adapter.dataset.http.dto.ExcelPreviewDTO;
import com.cyan.stargaze.dataset.adapter.dataset.http.dto.SqlColumnDTO;
import com.cyan.stargaze.dataset.adapter.dataset.http.dto.SqlPreviewDTO;
import com.cyan.stargaze.dataset.adapter.dataset.http.dto.TableFieldDTO;
import com.cyan.stargaze.dataset.adapter.dataset.http.dto.TableFieldsDTO;
import com.cyan.stargaze.dataset.adapter.dataset.http.dto.TableListDTO;
import com.cyan.stargaze.dataset.adapter.dataset.http.dto.TableMetaDTO;
import com.cyan.stargaze.dataset.application.dataset.bo.DatasetBO;
import com.cyan.stargaze.dataset.application.dataset.bo.DatasetFieldBO;
import com.cyan.stargaze.dataset.application.dataset.bo.DatasetListBO;
import com.cyan.stargaze.dataset.application.dataset.bo.DatasetStatisticsBO;
import com.cyan.stargaze.dataset.application.dataset.bo.DatasetSyncBO;
import com.cyan.stargaze.dataset.application.dataset.bo.ExcelPreviewBO;
import com.cyan.stargaze.dataset.application.dataset.bo.SqlPreviewBO;
import com.cyan.stargaze.dataset.domain.datasource.valobj.ColumnValObj;
import com.cyan.stargaze.dataset.domain.datasource.valobj.TableMetaValObj;
import com.cyan.stargaze.dataset.domain.datasource.valobj.TableSchemaValObj;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 数据集适配层转换(BO -> DTO)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Mapper(componentModel = "spring", uses = MapstructConvert.class)
public interface DatasetAdapterConvert {

    DatasetAdapterConvert INSTANCE = Mappers.getMapper(DatasetAdapterConvert.class);

    /** 列表 BO -> DTO */
    @Mapping(target = "version", source = "version", qualifiedByName = "versionLabel")
    DatasetListDTO toDatasetListDTO(DatasetListBO bo);

    List<DatasetListDTO> toDatasetListDTOList(List<DatasetListBO> bos);

    /** 详情 BO -> DTO */
    @Mapping(target = "version", source = "version", qualifiedByName = "versionLabel")
    @Mapping(target = "fields", source = "fields")
    DatasetDetailDTO toDatasetDetailDTO(DatasetBO bo);

    /** 字段 BO -> DTO */
    DatasetFieldDTO toDatasetFieldDTO(DatasetFieldBO bo);

    List<DatasetFieldDTO> toDatasetFieldDTOList(List<DatasetFieldBO> fields);

    /** 统计 BO -> DTO */
    DatasetStatisticsDTO toDatasetStatisticsDTO(DatasetStatisticsBO bo);

    /** 同步 BO -> DTO */
    DatasetSyncDTO toDatasetSyncDTO(DatasetSyncBO bo);

    DatasetSyncStatusDTO toDatasetSyncStatusDTO(DatasetSyncBO bo);

    /** SQL 预览 BO -> DTO(rows Map -> List<Object>) */
    default SqlPreviewDTO toSqlPreviewDTO(SqlPreviewBO bo) {
        if (bo == null) {
            return null;
        }
        List<SqlColumnDTO> columns = new ArrayList<>();
        if (bo.getColumns() != null) {
            for (ColumnValObj c : bo.getColumns()) {
                columns.add(new SqlColumnDTO().setName(c.getName()).setType(c.getDataType()));
            }
        }
        List<List<Object>> rows = new ArrayList<>();
        if (bo.getColumns() != null && bo.getRows() != null) {
            List<String> colNames = bo.getColumns().stream().map(ColumnValObj::getName).toList();
            for (Map<String, Object> row : bo.getRows()) {
                List<Object> values = new ArrayList<>(colNames.size());
                for (String name : colNames) {
                    values.add(row.get(name));
                }
                rows.add(values);
            }
        }
        return new SqlPreviewDTO()
                .setColumns(columns)
                .setRows(rows)
                .setTotalRows(bo.getTotalRows())
                .setExecutionTime(bo.getExecutionTime())
                .setIsTruncated(bo.getIsTruncated());
    }

    /** Excel 预览 BO -> DTO */
    default ExcelPreviewDTO toExcelPreviewDTO(ExcelPreviewBO bo) {
        if (bo == null) {
            return null;
        }
        List<ExcelColumnDTO> columns = new ArrayList<>();
        if (bo.getColumns() != null) {
            for (ExcelPreviewBO.ExcelColumnBO c : bo.getColumns()) {
                columns.add(new ExcelColumnDTO().setIndex(c.getIndex()).setName(c.getName()).setSuggestedType(c.getSuggestedType()));
            }
        }
        return new ExcelPreviewDTO()
                .setFileId(bo.getFileId())
                .setFileName(bo.getFileName())
                .setSheetName(bo.getSheetName())
                .setTotalRows(bo.getTotalRows())
                .setColumns(columns)
                .setRows(bo.getRows());
    }

    /** TableMetaValObj -> TableMetaDTO */
    TableMetaDTO toTableMetaDTO(TableMetaValObj valObj);

    List<TableMetaDTO> toTableMetaDTOList(List<TableMetaValObj> valObjs);

    /** TableSchemaValObj -> TableFieldsDTO(字段探查) */
    default TableFieldsDTO toTableFieldsDTO(TableSchemaValObj valObj) {
        List<TableFieldDTO> fields = new ArrayList<>();
        if (valObj != null && valObj.getColumns() != null) {
            for (ColumnValObj c : valObj.getColumns()) {
                fields.add(new TableFieldDTO()
                        .setFieldName(c.getName())
                        .setFieldType(c.getDataType())
                        .setIsNullable(c.getNullable())
                        .setIsPrimaryKey(c.getPrimaryKey())
                        .setFieldComment(c.getComment()));
            }
        }
        return new TableFieldsDTO()
                .setTableName(valObj == null ? null : valObj.getTableName())
                .setTableComment(valObj == null ? null : valObj.getTableComment())
                .setFields(fields);
    }

    /** version Integer -> "v" + n 字符串 */
    @Named("versionLabel")
    default String versionLabel(Integer version) {
        return version == null ? null : "v" + version;
    }

    /** BO 字段 -> 对外契约 DTO(/rpc 返回,推断逻辑类型) */
    default com.cyan.stargaze.dataset.client.dto.DatasetFieldDTO toClientDatasetFieldDTO(DatasetFieldBO bo) {
        if (bo == null) {
            return null;
        }
        return new com.cyan.stargaze.dataset.client.dto.DatasetFieldDTO()
                .setId(bo.getId())
                .setDatasetId(bo.getDatasetId())
                .setOriginName(bo.getFieldName())
                .setAlias(bo.getDisplayName())
                .setDataType(com.cyan.stargaze.dataset.infra.util.DataTypeInferrer.infer(bo.getDataType()))
                .setFieldType(bo.getFieldType());
    }

    /** BO 字段列表 -> 对外契约 DTO 列表 */
    default java.util.List<com.cyan.stargaze.dataset.client.dto.DatasetFieldDTO> toClientDatasetFieldDTOList(
            java.util.List<DatasetFieldBO> fields) {
        if (fields == null) {
            return java.util.List.of();
        }
        return fields.stream().map(this::toClientDatasetFieldDTO).toList();
    }

    /** BO 字段 -> 字段解析契约 DTO(/rpc 返回) */
    default com.cyan.stargaze.dataset.client.dto.ResolveFieldDTO toResolveFieldDTO(DatasetFieldBO bo) {
        if (bo == null) {
            return null;
        }
        return new com.cyan.stargaze.dataset.client.dto.ResolveFieldDTO()
                .setId(bo.getId())
                .setOriginName(bo.getFieldName())
                .setAlias(bo.getDisplayName())
                .setDataType(com.cyan.stargaze.dataset.infra.util.DataTypeInferrer.infer(bo.getDataType()))
                .setFieldType(bo.getFieldType());
    }
}
