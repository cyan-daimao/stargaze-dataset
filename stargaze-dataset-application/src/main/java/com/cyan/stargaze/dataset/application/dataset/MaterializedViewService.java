package com.cyan.stargaze.dataset.application.dataset;

import com.cyan.stargaze.dataset.application.dataset.cmd.MaterializedViewCmd;
import com.cyan.stargaze.dataset.domain.dataset.MaterializedView;

import java.util.List;

/**
 * 物化加速配置应用服务。
 *
 * @author cy.Y
 * @since 1.0.0
 */
public interface MaterializedViewService {

    MaterializedView create(MaterializedViewCmd cmd);

    MaterializedView update(MaterializedViewCmd cmd);

    void delete(String id);

    List<MaterializedView> listByDatasetId(String datasetId);
}
