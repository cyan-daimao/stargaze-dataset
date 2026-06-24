package com.cyan.stargaze.dataset.application.dataset.convert;

import com.cyan.arch.base.mapstruct.MapstructConvert;
import com.cyan.stargaze.dataset.application.dataset.bo.DatasetBO;
import com.cyan.stargaze.dataset.application.dataset.bo.DatasetFieldBO;
import com.cyan.stargaze.dataset.application.dataset.cmd.DatasetCreateCmd;
import com.cyan.stargaze.dataset.application.dataset.cmd.DatasetFieldCmd;
import com.cyan.stargaze.dataset.domain.dataset.Dataset;
import com.cyan.stargaze.dataset.domain.dataset.DatasetField;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 数据集应用层转换(Cmd -> Domain, Domain -> BO,含字段列表)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Mapper(componentModel = "spring", uses = MapstructConvert.class)
public interface DatasetAppConvert {

    DatasetAppConvert INSTANCE = Mappers.getMapper(DatasetAppConvert.class);

    /** Domain -> BO(详情,不含 config/fields,由 service 单独填充) */
    @Mapping(target = "sourceTypeName", source = "sourceType", qualifiedByName = "sourceTypeName")
    @Mapping(target = "datasourceId", source = "dataSourceId")
    @Mapping(target = "datasourceName", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "config", ignore = true)
    @Mapping(target = "fields", ignore = true)
    @Mapping(target = "statistics", ignore = true)
    DatasetBO toDatasetBO(Dataset dataset);

    /** Domain 字段 -> BO 字段(field 相关字段映射) */
    @Mapping(target = "fieldName", source = "originName")
    @Mapping(target = "sortOrder", source = "ord")
    DatasetFieldBO toDatasetFieldBO(DatasetField field);

    /** Domain 字段列表 -> BO 字段列表 */
    List<DatasetFieldBO> toDatasetFieldBOList(List<DatasetField> fields);

    /** Cmd 字段 -> Domain 字段 */
    @Mapping(target = "originName", source = "fieldName")
    @Mapping(target = "alias", ignore = true)
    @Mapping(target = "ord", source = "sortOrder")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "datasetId", ignore = true)
    @Mapping(target = "semanticType", ignore = true)
    @Mapping(target = "format", ignore = true)
    @Mapping(target = "dictionaryId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    DatasetField toDatasetField(DatasetFieldCmd cmd);

    /** Cmd 字段列表 -> Domain 字段列表 */
    List<DatasetField> toDatasetFieldList(List<DatasetFieldCmd> cmds);

    /** 来源类型 -> 展示名 */
    @Named("sourceTypeName")
    default String sourceTypeName(com.cyan.stargaze.dataset.enums.DatasetSourceType sourceType) {
        return sourceType == null ? null : sourceType.getDisplayName();
    }
}
