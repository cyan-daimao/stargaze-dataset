package com.cyan.stargaze.dataset.domain.dataset;

import com.cyan.arch.common.api.Assert;
import com.cyan.arch.common.api.SilentException;
import com.cyan.stargaze.dataset.domain.dataset.repository.DatasetRepository;
import com.cyan.stargaze.dataset.enums.DatasetSourceType;
import com.cyan.stargaze.dataset.enums.DatasetStatus;
import com.cyan.stargaze.dataset.enums.FieldType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据集领域对象(充血模型,聚合字段列表)。
 * <p>
 * 数据集是指标的物理来源,只定义物理字段与基础语义,业务口径上移到指标层。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Dataset {

    /** 主键 */
    private String id;

    /** 数据集名称(全局唯一) */
    private String name;

    /** 描述 */
    private String description;

    /** 来源类型(table/sql/join/excel/union) */
    private DatasetSourceType sourceType;

    /** 关联数据源 ID */
    private String dataSourceId;

    /** 来源配置(table/sql/join/excel config,jsonb 序列化字符串) */
    private String definition;

    /** 元数据刷新策略(jsonb 序列化字符串) */
    private String refreshConfig;

    /** 物化加速配置(jsonb 序列化字符串) */
    private String accelerations;

    /** 状态:draft/published/archived */
    private DatasetStatus status;

    /** 版本号 */
    private Integer version;

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

    /** 字段列表(聚合子实体) */
    private List<DatasetField> fields;

    /**
     * 校验
     */
    private void validate() {
        Assert.notBlank(this.name, new SilentException("数据集名称不能为空"));
        Assert.notNull(this.sourceType, new SilentException("数据集来源类型不能为空"));
        Assert.notBlank(this.definition, new SilentException("数据集配置不能为空"));
    }

    /**
     * 保存(新建,连同字段一起持久化)
     */
    public Dataset save(DatasetRepository repository) {
        validate();
        Dataset existing = repository.findByName(this.name);
        Assert.isNull(existing, new SilentException("数据集名称已存在"));
        if (fields != null) {
            fields.forEach(DatasetField::validate);
        }
        this.status = DatasetStatus.PUBLISHED;
        this.version = 1;
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
        return repository.save(this);
    }

    /**
     * 更新(版本号递增)
     */
    public Dataset update(DatasetRepository repository) {
        validate();
        Assert.notBlank(this.id, new SilentException("数据集 ID 不能为空"));
        if (fields != null) {
            fields.forEach(DatasetField::validate);
        }
        this.version = (this.version == null ? 1 : this.version) + 1;
        this.updatedAt = OffsetDateTime.now();
        return repository.update(this);
    }

    /**
     * 删除(逻辑删除,连带字段)
     */
    public void delete(DatasetRepository repository) {
        Assert.notBlank(this.id, new SilentException("数据集 ID 不能为空"));
        repository.deleteById(this.id);
    }

    /**
     * 刷新字段合并:按 originName 匹配,命中保留旧配置仅刷新 dataType/sourceTable,新增追加 ord,删除丢弃。
     * <p>
     * 保持 ord/alias/displayName/isEnabled/aggregation 等用户配置稳定,避免前端展示抖动。
     *
     * @param existing 既有字段
     * @param fresh    源端最新解析字段(含 originName/dataType/sourceTable)
     * @return 合并后的字段列表
     */
    public List<DatasetField> rebuildFields(List<DatasetField> existing, List<DatasetField> fresh) {
        Map<String, DatasetField> oldByOrigin = new LinkedHashMap<>();
        if (existing != null) {
            for (DatasetField f : existing) {
                oldByOrigin.put(f.getOriginName(), f);
            }
        }
        int maxOrd = oldByOrigin.values().stream()
                .mapToInt(f -> f.getOrd() == null ? 0 : f.getOrd())
                .max().orElse(0);
        List<DatasetField> merged = new ArrayList<>();
        if (fresh == null) {
            return merged;
        }
        for (DatasetField f : fresh) {
            DatasetField old = oldByOrigin.get(f.getOriginName());
            if (old != null) {
                // 命中:保留用户配置,仅刷新源端可变属性
                old.setDataType(f.getDataType());
                if (f.getSourceTable() != null) {
                    old.setSourceTable(f.getSourceTable());
                }
                merged.add(old);
            } else {
                // 新增:默认维度启用,追加 ord
                if (f.getAlias() == null) {
                    f.setAlias(f.getOriginName());
                }
                if (f.getDisplayName() == null) {
                    f.setDisplayName(f.getOriginName());
                }
                if (f.getFieldType() == null) {
                    f.setFieldType(FieldType.DIMENSION);
                }
                if (f.getIsEnabled() == null) {
                    f.setIsEnabled(true);
                }
                f.setOrd(++maxOrd);
                merged.add(f);
            }
        }
        return merged;
    }
}
