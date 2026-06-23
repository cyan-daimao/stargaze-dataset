package com.cyan.stargaze.dataset.application.dataset;

import com.cyan.stargaze.dataset.application.dataset.cmd.DatasetHierarchyCmd;
import com.cyan.stargaze.dataset.domain.dataset.DatasetHierarchy;

import java.util.List;

/**
 * 数据集维度层级应用服务。
 *
 * @author cy.Y
 * @since 1.0.0
 */
public interface DatasetHierarchyService {

    DatasetHierarchy create(DatasetHierarchyCmd cmd);

    DatasetHierarchy update(DatasetHierarchyCmd cmd);

    void delete(String id);

    List<DatasetHierarchy> listByDatasetId(String datasetId);
}
