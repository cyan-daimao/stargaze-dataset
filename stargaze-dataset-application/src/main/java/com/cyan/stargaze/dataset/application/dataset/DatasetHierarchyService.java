package com.cyan.stargaze.dataset.application.dataset;

import com.cyan.stargaze.dataset.application.dataset.bo.DatasetHierarchyBO;
import com.cyan.stargaze.dataset.application.dataset.cmd.DatasetHierarchyCmd;

import java.util.List;

/**
 * 数据集维度层级应用服务。
 *
 * @author cy.Y
 * @since 1.0.0
 */
public interface DatasetHierarchyService {

    DatasetHierarchyBO create(DatasetHierarchyCmd cmd);

    DatasetHierarchyBO update(DatasetHierarchyCmd cmd);

    void delete(String id);

    List<DatasetHierarchyBO> listByDatasetId(String datasetId);
}
