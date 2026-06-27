package com.cyan.stargaze.dataset.application.dataset;

import com.cyan.stargaze.dataset.application.dataset.bo.MaterializedViewBO;
import com.cyan.stargaze.dataset.application.dataset.cmd.MaterializedViewCmd;

import java.util.List;

/**
 * 物化加速配置应用服务。
 *
 * @author cy.Y
 * @since 1.0.0
 */
public interface MaterializedViewService {

    MaterializedViewBO create(MaterializedViewCmd cmd);

    MaterializedViewBO update(MaterializedViewCmd cmd);

    void delete(String id);

    List<MaterializedViewBO> listByDatasetId(String datasetId);
}
