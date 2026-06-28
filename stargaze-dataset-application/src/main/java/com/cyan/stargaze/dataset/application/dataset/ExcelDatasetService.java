package com.cyan.stargaze.dataset.application.dataset;

import com.cyan.stargaze.dataset.application.dataset.bo.DatasetFieldBO;
import com.cyan.stargaze.dataset.domain.dataset.config.ExcelConfig;
import com.cyan.stargaze.dataset.domain.datasource.valobj.TableSampleValObj;

import java.util.List;

/**
 * Excel 数据集应用服务:字段解析 / 预览 / 行数。
 *
 * @author cy.Y
 * @since 1.0.0
 */
public interface ExcelDatasetService {

    /**
     * 解析 Excel 为数据集字段列表
     */
    List<DatasetFieldBO> resolveFields(ExcelConfig config);

    /**
     * 预览 Excel 数据
     */
    TableSampleValObj preview(ExcelConfig config, int limit);

    /**
     * 读取全量 Excel 数据(受 Excel 配置上限限制)
     */
    TableSampleValObj readAllRows(ExcelConfig config);

    /**
     * Excel 行数(statistics 用)
     */
    Long rowCount(ExcelConfig config);
}
