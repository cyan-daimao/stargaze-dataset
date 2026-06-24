package com.cyan.stargaze.dataset.domain.dataset.config;

import com.alibaba.fastjson2.JSON;
import com.cyan.arch.common.api.Assert;
import com.cyan.arch.common.api.SilentException;
import com.cyan.stargaze.dataset.enums.DatasetSourceType;

/**
 * 数据集配置工厂:按 {@link DatasetSourceType} 显式解析/序列化 definition JSON。
 * <p>
 * 不开启 fastjson2 autoType,由调用方提供 sourceType 决定目标类。
 *
 * @author cy.Y
 * @since 1.0.0
 */
public final class DatasetConfigs {

    private DatasetConfigs() {
    }

    /**
     * 按 sourceType 解析 definition JSON。
     */
    public static DatasetConfig parse(DatasetSourceType sourceType, String json) {
        Assert.notNull(sourceType, new SilentException("数据集来源类型不能为空"));
        Assert.notBlank(json, new SilentException("数据集配置不能为空"));
        DatasetConfig config;
        try {
            switch (sourceType) {
                case TABLE:
                    config = JSON.parseObject(json, TableConfig.class);
                    break;
                case SQL:
                    config = JSON.parseObject(json, SqlConfig.class);
                    break;
                case JOIN:
                    config = JSON.parseObject(json, JoinConfig.class);
                    break;
                case EXCEL:
                    config = JSON.parseObject(json, ExcelConfig.class);
                    break;
                default:
                    throw new SilentException("暂不支持的数据集来源类型: " + sourceType);
            }
        } catch (SilentException e) {
            throw e;
        } catch (Exception e) {
            throw new SilentException("数据集配置解析失败: " + e.getMessage());
        }
        Assert.notNull(config, new SilentException("数据集配置解析为空"));
        config.validate();
        return config;
    }

    /**
     * 序列化为 JSON 字符串。
     */
    public static String toJson(DatasetConfig config) {
        Assert.notNull(config, new SilentException("数据集配置不能为空"));
        return JSON.toJSONString(config);
    }
}
