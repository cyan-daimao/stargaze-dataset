package com.cyan.stargaze.dataset.domain.dataset;

import com.cyan.arch.common.api.Assert;
import com.cyan.arch.common.api.SilentException;
import com.cyan.stargaze.dataset.domain.dataset.repository.DatasetFileRepository;
import com.cyan.stargaze.dataset.enums.CommonStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;

/**
 * 数据集文件领域对象(上传的 Excel/CSV 文件登记记录)。
 * <p>
 * 文件实体存于 S3/对象存储,本对象仅登记元信息(object_key 等)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class DatasetFile {

    /** 主键 */
    private String id;

    /** 原始文件名 */
    private String fileName;

    /** S3/MinIO 对象 key */
    private String objectKey;

    /** 文件类型 */
    private String contentType;

    /** 文件大小(字节) */
    private Long size;

    /** 状态:active/error/deleted */
    private CommonStatus status;

    /** 创建人 */
    private String createdBy;

    /** 修改人 */
    private String updatedBy;

    /** 创建时间 */
    private OffsetDateTime createdAt;

    /** 更新时间 */
    private OffsetDateTime updatedAt;

    /** 逻辑删除时间 */
    private OffsetDateTime deletedAt;

    /**
     * 校验必填项
     */
    private void validate() {
        Assert.notBlank(this.fileName, new SilentException("文件名不能为空"));
        Assert.notBlank(this.objectKey, new SilentException("对象 key 不能为空"));
    }

    /**
     * 保存(新建)
     */
    public DatasetFile save(DatasetFileRepository repository) {
        validate();
        this.status = CommonStatus.ACTIVE;
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
        return repository.save(this);
    }
}
