package com.cyan.stargaze.dataset.application.dataset.impl;

import com.cyan.arch.common.api.Assert;
import com.cyan.arch.common.api.SilentException;
import com.cyan.stargaze.dataset.application.dataset.MaterializedViewService;
import com.cyan.stargaze.dataset.application.dataset.bo.MaterializedViewBO;
import com.cyan.stargaze.dataset.application.dataset.cmd.MaterializedViewCmd;
import com.cyan.stargaze.dataset.application.dataset.convert.DatasetAuxiliaryAppConvert;
import com.cyan.stargaze.dataset.domain.dataset.MaterializedView;
import com.cyan.stargaze.dataset.domain.dataset.repository.MaterializedViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 物化加速配置应用服务实现。
 * <p>
 * 配置管理一期完成;实际物化表同步到 StarRocks 由调度触发(见 MaterializedViewScheduler)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class MaterializedViewServiceImpl implements MaterializedViewService {

    private final MaterializedViewRepository repository;
    private final DatasetAuxiliaryAppConvert convert;

    @Override
    @Transactional
    public MaterializedViewBO create(MaterializedViewCmd cmd) {
        MaterializedView view = convert.toMaterializedView(cmd);
        view = view.save(repository);
        return convert.toMaterializedViewBO(view);
    }

    @Override
    @Transactional
    public MaterializedViewBO update(MaterializedViewCmd cmd) {
        MaterializedView existing = repository.findById(cmd.getId());
        Assert.notNull(existing, new SilentException("物化加速配置不存在"));
        MaterializedView view = convert.toMaterializedView(cmd);
        view.setId(existing.getId());
        view = view.update(repository);
        return convert.toMaterializedViewBO(view);
    }

    @Override
    @Transactional
    public void delete(String id) {
        MaterializedView view = repository.findById(id);
        Assert.notNull(view, new SilentException("物化加速配置不存在"));
        view.delete(repository);
    }

    @Override
    public List<MaterializedViewBO> listByDatasetId(String datasetId) {
        return convert.toMaterializedViewBOList(repository.listByDatasetId(datasetId));
    }
}
