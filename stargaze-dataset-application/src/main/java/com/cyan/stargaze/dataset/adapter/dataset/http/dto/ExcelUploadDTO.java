package com.cyan.stargaze.dataset.adapter.dataset.http.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Excel 上传结果 DTO。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ExcelUploadDTO {

    private String fileId;
    private String fileName;
    private Long fileSize;
    private List<String> sheetNames;
    private String previewUrl;
}
