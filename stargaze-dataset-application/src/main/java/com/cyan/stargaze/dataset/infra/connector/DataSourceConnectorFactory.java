package com.cyan.stargaze.dataset.infra.connector;

import com.cyan.arch.common.api.SilentException;
import com.cyan.stargaze.dataset.enums.DatasourceType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * 数据源适配器工厂:按 {@link DatasourceType} 分发到对应 {@link DataSourceConnector}。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class DataSourceConnectorFactory {

    private final List<DataSourceConnector> connectors;

    private Map<DatasourceType, DataSourceConnector> registry;

    /**
     * 获取适配器(按类型)
     */
    public DataSourceConnector get(DatasourceType type) {
        if (registry == null) {
            registry = new EnumMap<>(DatasourceType.class);
            for (DataSourceConnector connector : connectors) {
                registry.put(connector.supportType(), connector);
            }
        }
        DataSourceConnector connector = registry.get(type);
        if (connector == null) {
            throw new SilentException("暂不支持的数据源类型: " + type);
        }
        return connector;
    }
}
