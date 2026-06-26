package com.cyan.stargaze.dataset.application.dataset.bo;

import com.cyan.stargaze.dataset.enums.CommonStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 数据集文件业务对象。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class DatasetFileBO {

    /** 文件 ID */
    private String id;

    /** 原始文件名 */
    private String fileName;

    /** S3 对象 key */
    private String objectKey;

    /** 文件类型 */
    private String contentType;

    /** 文件大小(字节) */
    private Long size;

    /** 状态 */
    private CommonStatus status;
}
