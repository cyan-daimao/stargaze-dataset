package com.cyan.stargaze.dataset.domain.dataset.config;

import com.cyan.arch.common.api.Assert;
import com.cyan.arch.common.api.SilentException;
import com.cyan.stargaze.dataset.enums.DatasetSourceType;
import com.cyan.stargaze.dataset.enums.UpdateMode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Excel 数据集配置。
 * <p>
 * 执行所需的 objectKey/headerRow 由 fileId 关联 {@code DatasetFile} 查得,不入本配置。
 *
 * <pre>
 * { "fileId": "123", "fileName": "Q2.xlsx", "sheetName": "Sheet1", "updateMode": "OVERWRITE" }
 * </pre>
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class ExcelConfig extends DatasetConfig {

    /** 文件 ID */
    private String fileId;

    /** 原始文件名 */
    private String fileName;

    /** sheet 名称 */
    private String sheetName;

    /** 数据更新方式(OVERWRITE/APPEND) */
    private UpdateMode updateMode;

    @Override
    public DatasetSourceType type() {
        return DatasetSourceType.EXCEL;
    }

    @Override
    public void validate() {
        Assert.notBlank(this.fileId, new SilentException("Excel 数据集配置缺少文件 ID"));
        Assert.notBlank(this.sheetName, new SilentException("Excel 数据集配置缺少 sheet 名称"));
    }
}
