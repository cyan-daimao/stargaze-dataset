package com.cyan.stargaze.dataset.infra.persistence.datasource.convert;

import com.alibaba.fastjson2.JSON;
import com.cyan.arch.base.mapstruct.MapstructConvert;
import com.cyan.stargaze.dataset.domain.datasource.DataSource;
import com.cyan.stargaze.dataset.domain.datasource.valobj.DataSourceConfig;
import com.cyan.stargaze.dataset.domain.datasource.valobj.PoolConfig;
import com.cyan.stargaze.dataset.infra.persistence.datasource.dos.DataSourceDO;
import com.cyan.stargaze.dataset.infra.util.AesCryptoUtil;
import com.cyan.stargaze.dataset.infra.util.IdUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 数据源 DO <-> Domain 转换。
 * <p>
 * config_enc(密文)/pool_config(jsonb) <-> DataSourceConfig/PoolConfig(明文对象),
 * 加解密由 {@link AesCryptoUtil} 完成。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Mapper(componentModel = "spring", uses = MapstructConvert.class)
public abstract class DataSourceInfraConvert {

    public static final DataSourceInfraConvert INSTANCE = Mappers.getMapper(DataSourceInfraConvert.class);

    @Autowired
    protected AesCryptoUtil aesCryptoUtil;

    /**
     * DO -> Domain
     */
    @Mapping(target = "id", source = "id", qualifiedByName = "longToString")
    @Mapping(target = "config", source = "configEnc", qualifiedByName = "decryptToConfig")
    @Mapping(target = "poolConfig", source = "poolConfig", qualifiedByName = "jsonToPoolConfig")
    @Mapping(target = "createdBy", source = "createdBy", qualifiedByName = "longToString")
    @Mapping(target = "updatedBy", source = "updatedBy", qualifiedByName = "longToString")
    public abstract DataSource toDataSource(DataSourceDO dataSourceDO);

    /**
     * Domain -> DO
     */
    @Mapping(target = "id", source = "id", qualifiedByName = "stringToLong")
    @Mapping(target = "configEnc", source = "config", qualifiedByName = "encryptConfig")
    @Mapping(target = "poolConfig", source = "poolConfig", qualifiedByName = "poolConfigToJson")
    @Mapping(target = "createdBy", source = "createdBy", qualifiedByName = "stringToLong")
    @Mapping(target = "updatedBy", source = "updatedBy", qualifiedByName = "stringToLong")
    public abstract DataSourceDO toDataSourceDO(DataSource dataSource);

    @Named("longToString")
    protected String longToString(Long value) {
        return IdUtil.toString(value);
    }

    @Named("stringToLong")
    protected Long stringToLong(String value) {
        return IdUtil.toLong(value);
    }

    @Named("decryptToConfig")
    protected DataSourceConfig decryptToConfig(String configEnc) {
        if (configEnc == null || configEnc.isBlank()) {
            return null;
        }
        return JSON.parseObject(aesCryptoUtil.decrypt(configEnc), DataSourceConfig.class);
    }

    @Named("encryptConfig")
    protected String encryptConfig(DataSourceConfig config) {
        if (config == null) {
            return null;
        }
        return aesCryptoUtil.encrypt(JSON.toJSONString(config));
    }

    @Named("jsonToPoolConfig")
    protected PoolConfig jsonToPoolConfig(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        return JSON.parseObject(json, PoolConfig.class);
    }

    @Named("poolConfigToJson")
    protected String poolConfigToJson(PoolConfig poolConfig) {
        if (poolConfig == null) {
            return null;
        }
        return JSON.toJSONString(poolConfig);
    }
}
