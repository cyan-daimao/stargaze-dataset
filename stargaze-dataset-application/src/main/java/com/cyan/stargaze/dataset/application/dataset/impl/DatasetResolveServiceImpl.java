package com.cyan.stargaze.dataset.application.dataset.impl;

import com.cyan.arch.common.api.Assert;
import com.cyan.arch.common.api.SilentException;
import com.cyan.stargaze.dataset.application.dataset.DatasetResolveService;
import com.cyan.stargaze.dataset.application.dataset.ExcelDatasetService;
import com.cyan.stargaze.dataset.application.dataset.JoinDatasetService;
import com.cyan.stargaze.dataset.application.datasource.SqlDatasetService;
import com.cyan.stargaze.dataset.application.dataset.bo.DatasetFieldBO;
import com.cyan.stargaze.dataset.application.dataset.cmd.DatasetCreateCmd;
import com.cyan.stargaze.dataset.application.datasource.DatasourceService;
import com.cyan.stargaze.dataset.domain.datasource.valobj.ColumnValObj;
import com.cyan.stargaze.dataset.domain.datasource.valobj.TableSampleValObj;
import com.cyan.stargaze.dataset.domain.datasource.valobj.TableSchemaValObj;
import com.cyan.stargaze.dataset.domain.dataset.config.ExcelConfig;
import com.cyan.stargaze.dataset.domain.dataset.config.JoinConfig;
import com.cyan.stargaze.dataset.domain.dataset.config.SqlConfig;
import com.cyan.stargaze.dataset.domain.dataset.config.TableConfig;
import com.cyan.stargaze.dataset.enums.FieldType;
import com.cyan.stargaze.dataset.infra.util.DataTypeInferrer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据集来源字段解析服务实现:按 sourceType 分发到 table/sql/join/excel。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class DatasetResolveServiceImpl implements DatasetResolveService {

    private final DatasourceService datasourceService;
    private final SqlDatasetService sqlDatasetService;
    private final JoinDatasetService joinDatasetService;
    private final ExcelDatasetService excelDatasetService;

    @Override
    public List<DatasetFieldBO> resolveFields(DatasetCreateCmd cmd) {
        switch (cmd.getSourceType()) {
            case TABLE:
                Assert.notNull(cmd.getTableConfig(), new SilentException("缺少 table_config"));
                return resolveTable(cmd.getDatasourceId(), cmd.getTableConfig());
            case SQL:
                Assert.notNull(cmd.getSqlConfig(), new SilentException("缺少 sql_config"));
                SqlConfig sqlConfig = cmd.getSqlConfig();
                return sqlDatasetService.resolveFields(cmd.getDatasourceId(), sqlConfig.getSql(), sqlConfig.getSchema());
            case JOIN:
                Assert.notNull(cmd.getJoinConfig(), new SilentException("缺少 join_config"));
                return joinDatasetService.resolveFields(cmd.getDatasourceId(), cmd.getJoinConfig());
            case EXCEL:
                Assert.notNull(cmd.getExcelConfig(), new SilentException("缺少 excel_config"));
                return excelDatasetService.resolveFields(cmd.getExcelConfig());
            default:
                throw new SilentException("暂不支持的数据集来源类型: " + cmd.getSourceType());
        }
    }

    private List<DatasetFieldBO> resolveTable(String datasourceId, TableConfig config) {
        Assert.notBlank(datasourceId, new SilentException("该来源类型必须指定数据源 ID"));
        TableSchemaValObj schema = datasourceService.describeTable(datasourceId, config.getSchema(), config.getTableName());
        List<DatasetFieldBO> fields = new ArrayList<>();
        int ord = 1;
        for (ColumnValObj column : schema.getColumns()) {
            fields.add(new DatasetFieldBO()
                    .setFieldName(column.getName())
                    .setDisplayName(column.getName())
                    .setDataType(column.getDataType())
                    .setFieldType(FieldType.DIMENSION)
                    .setSourceTable(config.getTableName())
                    .setIsEnabled(true)
                    .setSortOrder(ord++));
        }
        return fields;
    }
}
