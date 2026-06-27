package com.cyan.stargaze.dataset.application.dataset;

import com.cyan.stargaze.dataset.application.dataset.bo.DatasetFileBO;
import com.cyan.stargaze.dataset.application.dataset.bo.ExcelPreviewBO;
import com.cyan.stargaze.dataset.application.dataset.bo.ExcelSheetBO;
import com.cyan.stargaze.dataset.application.dataset.bo.TableSampleBO;
import com.cyan.stargaze.dataset.application.dataset.bo.TableSchemaBO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 数据集文件应用服务:上传 / sheet 探查 / schema / 采样 / 预览。
 *
 * @author cy.Y
 * @since 1.0.0
 */
public interface DatasetFileService {

    /**
     * 上传文件
     */
    DatasetFileBO upload(MultipartFile file);

    /**
     * 列出 sheet
     */
    List<ExcelSheetBO> listSheets(String fileId);

    /**
     * 解析 sheet 字段结构
     */
    TableSchemaBO schema(String fileId, String sheetName, Integer headerRow);

    /**
     * 采样 sheet 数据
     */
    TableSampleBO sample(String fileId, String sheetName, Integer headerRow, int limit);

    /**
     * 预览(列带序号/推断类型 + 行数组)
     */
    ExcelPreviewBO preview(String fileId, String sheetName, Integer headerRow, int limit);

    /**
     * 查询文件登记记录(供 Excel 数据集获取 objectKey)
     */
    DatasetFileBO getFile(String fileId);
}
