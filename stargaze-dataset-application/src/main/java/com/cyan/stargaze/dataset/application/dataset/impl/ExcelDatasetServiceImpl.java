package com.cyan.stargaze.dataset.application.dataset.impl;

import com.cyan.stargaze.dataset.application.dataset.DatasetFileService;
import com.cyan.stargaze.dataset.application.dataset.ExcelDatasetService;
import com.cyan.stargaze.dataset.application.dataset.bo.DatasetFieldBO;
import com.cyan.stargaze.dataset.domain.dataset.DatasetFile;
import com.cyan.stargaze.dataset.domain.dataset.config.ExcelConfig;
import com.cyan.stargaze.dataset.domain.datasource.valobj.ColumnValObj;
import com.cyan.stargaze.dataset.domain.datasource.valobj.TableSampleValObj;
import com.cyan.stargaze.dataset.domain.datasource.valobj.TableSchemaValObj;
import com.cyan.stargaze.dataset.enums.FieldType;
import com.cyan.stargaze.dataset.infra.util.DataTypeInferrer;
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
    public List<DatasetFieldBO> resolveFields(String workspaceId, ExcelConfig config) {
        DatasetFile file = datasetFileService.getFile(config.getFileId());
        TableSchemaValObj schema = datasetFileService.schema(config.getFileId(), config.getSheetName(), 1);
        List<DatasetFieldBO> fields = new ArrayList<>();
        int ord = 1;
        for (ColumnValObj column : schema.getColumns()) {
            fields.add(new DatasetFieldBO()
                    .setFieldName(column.getName())
                    .setDisplayName(column.getName())
                    .setDataType(DataTypeInferrer.infer(column.getDataType()).toDisplayType())
                    .setFieldType(FieldType.DIMENSION)
                    .setSourceTable(file == null ? null : file.getFileName())
                    .setIsEnabled(true)
                    .setSortOrder(ord++));
        }
        return fields;
    }

    @Override
    public TableSampleValObj preview(String workspaceId, ExcelConfig config, int limit) {
        return datasetFileService.sample(config.getFileId(), config.getSheetName(), 1, limit);
    }

    @Override
    public Long rowCount(String workspaceId, ExcelConfig config) {
        try {
            TableSchemaValObj schema = datasetFileService.schema(config.getFileId(), config.getSheetName(), 1);
            return schema.getRowCount();
        } catch (Exception e) {
            return null;
        }
    }
}
