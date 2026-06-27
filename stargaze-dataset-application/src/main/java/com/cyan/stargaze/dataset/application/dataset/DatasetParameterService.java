package com.cyan.stargaze.dataset.application.dataset;

import com.cyan.stargaze.dataset.application.dataset.bo.DatasetParameterBO;
import com.cyan.stargaze.dataset.application.dataset.cmd.DatasetParameterCmd;

import java.util.List;

/**
 * 数据集参数字段应用服务。
 *
 * @author cy.Y
 * @since 1.0.0
 */
public interface DatasetParameterService {

    DatasetParameterBO create(DatasetParameterCmd cmd);

    DatasetParameterBO update(DatasetParameterCmd cmd);

    void delete(String id);

    List<DatasetParameterBO> listByDatasetId(String datasetId);
}
