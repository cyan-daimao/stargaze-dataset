package com.cyan.stargaze.dataset.application.dataset.impl;

import com.cyan.stargaze.dataset.application.dataset.DatasetFileService;
import com.cyan.stargaze.dataset.application.dataset.ExcelDatasetService;
import com.cyan.stargaze.dataset.application.dataset.bo.ColumnBO;
import com.cyan.stargaze.dataset.application.dataset.bo.DatasetFieldBO;
import com.cyan.stargaze.dataset.application.dataset.bo.DatasetFileBO;
import com.cyan.stargaze.dataset.application.dataset.bo.TableSampleBO;
import com.cyan.stargaze.dataset.application.dataset.bo.TableSchemaBO;
import com.cyan.stargaze.dataset.domain.dataset.config.ExcelConfig;
import com.cyan.stargaze.dataset.domain.datasource.valobj.TableSampleValObj;
import com.cyan.stargaze.dataset.enums.FieldType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Excel 数据集应用服务实现(委托 DatasetFileService 解析/采样)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class ExcelDatasetServiceImpl implements ExcelDatasetService {

    private final DatasetFileService datasetFileService;

    @Override
    public List<DatasetFieldBO> resolveFields(ExcelConfig config) {
        DatasetFileBO file = datasetFileService.getFile(config.getFileId());
        TableSchemaBO schema = datasetFileService.schema(config.getFileId(), config.getSheetName(), 1);
        List<DatasetFieldBO> fields = new ArrayList<>();
        int ord = 1;
        for (ColumnBO column : schema.getColumns()) {
            fields.add(new DatasetFieldBO()
                    .setFieldName(column.getName())
                    .setDisplayName(column.getName())
                    .setDataType(column.getSuggestedType())
                    .setFieldType(FieldType.DIMENSION)
                    .setSourceTable(file == null ? null : file.getFileName())
                    .setIsEnabled(true)
                    .setSortOrder(ord++));
        }
        return fields;
    }

    @Override
    public TableSampleValObj preview(ExcelConfig config, int limit) {
        TableSampleBO sample = datasetFileService.sample(config.getFileId(), config.getSheetName(), 1, limit);
        // 为了保持 ExcelDatasetService 契约与 SQL/JOIN 预览返回类型一致,临时做 BO -> Domain 值对象转换
        return new TableSampleValObj().setColumns(sample.getColumns()).setRows(sample.getRows());
    }

    @Override
    public Long rowCount(ExcelConfig config) {
        try {
            TableSchemaBO schema = datasetFileService.schema(config.getFileId(), config.getSheetName(), 1);
            return schema.getRowCount();
        } catch (Exception e) {
            return null;
        }
    }
}
