package com.cyan.stargaze.dataset.application.dataset.convert;

import com.cyan.arch.base.mapstruct.MapstructConvert;
import com.cyan.stargaze.dataset.application.dataset.bo.ColumnBO;
import com.cyan.stargaze.dataset.application.dataset.bo.DatasetFileBO;
import com.cyan.stargaze.dataset.application.dataset.bo.ExcelSheetBO;
import com.cyan.stargaze.dataset.application.dataset.bo.TableSampleBO;
import com.cyan.stargaze.dataset.application.dataset.bo.TableSchemaBO;
import com.cyan.stargaze.dataset.domain.dataset.DatasetFile;
import com.cyan.stargaze.dataset.domain.dataset.valobj.ExcelSheetValObj;
import com.cyan.stargaze.dataset.domain.datasource.valobj.ColumnValObj;
import com.cyan.stargaze.dataset.domain.datasource.valobj.TableSampleValObj;
import com.cyan.stargaze.dataset.domain.datasource.valobj.TableSchemaValObj;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 数据集文件 Domain <-> BO 转换。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Mapper(componentModel = "spring", uses = MapstructConvert.class)
public interface DatasetFileAppConvert {

    DatasetFileAppConvert INSTANCE = Mappers.getMapper(DatasetFileAppConvert.class);

    /** Domain -> BO */
    DatasetFileBO toDatasetFileBO(DatasetFile datasetFile);

    /** Excel Sheet 值对象 -> BO */
    ExcelSheetBO toExcelSheetBO(ExcelSheetValObj valObj);

    List<ExcelSheetBO> toExcelSheetBOList(List<ExcelSheetValObj> valObjs);

    /** 字段值对象 -> BO(含推断逻辑类型) */
    @Mapping(target = "suggestedType", source = "dataType", qualifiedByName = "suggestedType")
    ColumnBO toColumnBO(ColumnValObj valObj);

    List<ColumnBO> toColumnBOList(List<ColumnValObj> valObjs);

    /** 表结构值对象 -> BO */
    TableSchemaBO toTableSchemaBO(TableSchemaValObj valObj);

    /** 表采样值对象 -> BO */
    TableSampleBO toTableSampleBO(TableSampleValObj valObj);

    /** 源库类型 -> 推断逻辑类型展示值 */
    @Named("suggestedType")
    default String suggestedType(String dataType) {
        return com.cyan.stargaze.dataset.domain.dataset.valobj.DataTypeInferrer.infer(dataType).toDisplayType();
    }
}
