package com.cyan.stargaze.dataset.application.dataset.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Excel 预览结果 BO(列带序号/推断类型,行为数组)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ExcelPreviewBO {

    /** 文件 ID */
    private String fileId;

    /** 文件名 */
    private String fileName;

    /** sheet 名称 */
    private String sheetName;

    /** 总行数 */
    private Long totalRows;

    /** 列(index/name/suggestedType) */
    private List<ExcelColumnBO> columns;

    /** 采样行(每行为值数组,按 columns 顺序) */
    private List<List<Object>> rows;

    /**
     * Excel 列信息。
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class ExcelColumnBO {
        /** 列序号(0 起始) */
        private Integer index;
        /** 列名 */
        private String name;
        /** 推断类型(VARCHAR/INT/DECIMAL/DATE 等) */
        private String suggestedType;
    }
}
