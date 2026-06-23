package com.cyan.stargaze.dataset.application.dataset.convert;

import com.cyan.arch.base.mapstruct.MapstructConvert;
import com.cyan.stargaze.dataset.application.dataset.bo.DatasetBO;
import com.cyan.stargaze.dataset.application.dataset.bo.DatasetFieldBO;
import com.cyan.stargaze.dataset.application.dataset.cmd.DatasetCmd;
import com.cyan.stargaze.dataset.application.dataset.cmd.DatasetFieldCmd;
import com.cyan.stargaze.dataset.domain.dataset.Dataset;
import com.cyan.stargaze.dataset.domain.dataset.DatasetField;
import org.mapstruct.Mapper;
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

    /** Domain -> BO */
    DatasetBO toDatasetBO(Dataset dataset);

    /** Domain 字段 -> BO 字段 */
    DatasetFieldBO toDatasetFieldBO(DatasetField field);

    /** Domain 字段列表 -> BO 字段列表 */
    List<DatasetFieldBO> toDatasetFieldBOList(List<DatasetField> fields);

    /** Cmd -> Domain */
    Dataset toDataset(DatasetCmd cmd);

    /** Cmd 字段 -> Domain 字段 */
    DatasetField toDatasetField(DatasetFieldCmd cmd);

    /** Cmd 字段列表 -> Domain 字段列表 */
    List<DatasetField> toDatasetFieldList(List<DatasetFieldCmd> cmds);
}
