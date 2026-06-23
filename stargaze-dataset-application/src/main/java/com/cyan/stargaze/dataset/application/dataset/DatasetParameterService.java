package com.cyan.stargaze.dataset.application.dataset;

import com.cyan.stargaze.dataset.application.dataset.cmd.DatasetParameterCmd;
import com.cyan.stargaze.dataset.domain.dataset.DatasetParameter;

import java.util.List;

/**
 * 数据集参数字段应用服务。
 *
 * @author cy.Y
 * @since 1.0.0
 */
public interface DatasetParameterService {

    DatasetParameter create(DatasetParameterCmd cmd);

    DatasetParameter update(DatasetParameterCmd cmd);

    void delete(String id);

    List<DatasetParameter> listByDatasetId(String datasetId);
}
