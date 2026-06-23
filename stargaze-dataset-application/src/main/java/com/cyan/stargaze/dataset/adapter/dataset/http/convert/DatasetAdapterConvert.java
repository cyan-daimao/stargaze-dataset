package com.cyan.stargaze.dataset.adapter.dataset.http.convert;

import com.cyan.arch.base.mapstruct.MapstructConvert;
import com.cyan.stargaze.dataset.adapter.dataset.http.dto.DatasetDTO;
import com.cyan.stargaze.dataset.adapter.dataset.http.dto.DatasetFieldDTO;
import com.cyan.stargaze.dataset.application.dataset.bo.DatasetBO;
import com.cyan.stargaze.dataset.application.dataset.bo.DatasetFieldBO;
import com.cyan.stargaze.dataset.client.dto.ResolveFieldDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 数据集适配层转换(BO -> DTO,含对外契约 DTO)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Mapper(componentModel = "spring", uses = MapstructConvert.class)
public interface DatasetAdapterConvert {

    DatasetAdapterConvert INSTANCE = Mappers.getMapper(DatasetAdapterConvert.class);

    /** BO -> 前端 DTO */
    DatasetDTO toDatasetDTO(DatasetBO bo);

    /** BO 字段 -> 前端 DTO 字段 */
    DatasetFieldDTO toDatasetFieldDTO(DatasetFieldBO bo);

    /** BO 字段列表 -> 前端 DTO 字段列表 */
    List<DatasetFieldDTO> toDatasetFieldDTOList(List<DatasetFieldBO> fields);

    /** BO 字段 -> 对外契约 DTO(/rpc 返回) */
    com.cyan.stargaze.dataset.client.dto.DatasetFieldDTO toClientDatasetFieldDTO(DatasetFieldBO bo);

    /** BO 字段列表 -> 对外契约 DTO 列表 */
    List<com.cyan.stargaze.dataset.client.dto.DatasetFieldDTO> toClientDatasetFieldDTOList(List<DatasetFieldBO> fields);

    /** BO 字段 -> 字段解析契约 DTO */
    ResolveFieldDTO toResolveFieldDTO(DatasetFieldBO bo);
}
