package com.cyan.stargaze.dataset.domain.datasource;

import com.cyan.arch.common.api.Assert;
import com.cyan.arch.common.api.SilentException;
import com.cyan.stargaze.dataset.domain.datasource.repository.DataSourceRepository;
import com.cyan.stargaze.dataset.domain.datasource.valobj.DataSourceConfig;
import com.cyan.stargaze.dataset.domain.datasource.valobj.PoolConfig;
import com.cyan.stargaze.dataset.enums.CommonStatus;
import com.cyan.stargaze.dataset.enums.DatasourceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;

/**
 * 数据源领域对象(充血模型)。
 * <p>
 * 管理外部数据源连接配置,密码明文仅存内存,持久化时加密为 config_enc。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class DataSource {

    /** 主键 */
    private String id;

    /** 数据源名称(全局唯一) */
    private String name;

    /** 数据源类型 */
    private DatasourceType type;

    /** 连接配置(明文) */
    private DataSourceConfig config;

    /** 连接池配置 */
    private PoolConfig poolConfig;

    /** 状态:active/error/inactive */
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
        Assert.notBlank(this.name, new SilentException("数据源名称不能为空"));
        Assert.notNull(this.type, new SilentException("数据源类型不能为空"));
        Assert.notNull(this.config, new SilentException("数据源连接配置不能为空"));
        Assert.notBlank(this.config.getHost(), new SilentException("数据源主机不能为空"));
        Assert.notNull(this.config.getPort(), new SilentException("数据源端口不能为空"));
        Assert.notBlank(this.config.getUsername(), new SilentException("数据源用户名不能为空"));
    }

    /**
     * 保存(新建)
     */
    public DataSource save(DataSourceRepository repository) {
        validate();
        // 名称全局唯一校验
        DataSource existing = repository.findByName(this.name);
        Assert.isNull(existing, new SilentException("数据源名称已存在"));
        this.status = CommonStatus.ACTIVE;
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
        return repository.save(this);
    }

    /**
     * 更新
     */
    public DataSource update(DataSourceRepository repository) {
        validate();
        Assert.notBlank(this.id, new SilentException("数据源 ID 不能为空"));
        this.updatedAt = OffsetDateTime.now();
        return repository.update(this);
    }

    /**
     * 删除(逻辑删除)
     */
    public void delete(DataSourceRepository repository) {
        Assert.notBlank(this.id, new SilentException("数据源 ID 不能为空"));
        repository.deleteById(this.id);
    }

    /**
     * 标记为异常状态(连接测试失败)
     */
    public void markError() {
        this.status = CommonStatus.ERROR;
        this.updatedAt = OffsetDateTime.now();
    }

    /**
     * 标记为正常状态(连接测试成功)
     */
    public void markActive() {
        this.status = CommonStatus.ACTIVE;
        this.updatedAt = OffsetDateTime.now();
    }
}
