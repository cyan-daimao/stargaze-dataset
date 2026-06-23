package com.cyan.stargaze.dataset.infra.persistence.dataset.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cyan.stargaze.dataset.domain.dataset.MaterializedView;
import com.cyan.stargaze.dataset.domain.dataset.repository.MaterializedViewRepository;
import com.cyan.stargaze.dataset.infra.persistence.dataset.convert.MaterializedViewInfraConvert;
import com.cyan.stargaze.dataset.infra.persistence.dataset.dos.MaterializedViewDO;
import com.cyan.stargaze.dataset.infra.persistence.dataset.mappers.MaterializedViewMapper;
import com.cyan.stargaze.dataset.infra.util.IdUtil;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 物化加速配置仓储实现。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Repository
public class MaterializedViewRepositoryImpl implements MaterializedViewRepository {

    private final MaterializedViewMapper mapper;
    private final MaterializedViewInfraConvert convert;

    public MaterializedViewRepositoryImpl(MaterializedViewMapper mapper, MaterializedViewInfraConvert convert) {
        this.mapper = mapper;
        this.convert = convert;
    }

    @Override
    public MaterializedView findById(String id) {
        MaterializedViewDO viewDO = mapper.selectById(IdUtil.toLong(id));
        return viewDO == null ? null : convert.toMaterializedView(viewDO);
    }

    @Override
    public List<MaterializedView> listByDatasetId(String datasetId) {
        LambdaQueryWrapper<MaterializedViewDO> wrapper = new LambdaQueryWrapper<MaterializedViewDO>()
                .eq(MaterializedViewDO::getDatasetId, IdUtil.toLong(datasetId));
        return mapper.selectList(wrapper).stream()
                .map(convert::toMaterializedView)
                .toList();
    }

    @Override
    public MaterializedView save(MaterializedView view) {
        MaterializedViewDO viewDO = convert.toMaterializedViewDO(view);
        mapper.insert(viewDO);
        return findById(IdUtil.toString(viewDO.getId()));
    }

    @Override
    public MaterializedView update(MaterializedView view) {
        MaterializedViewDO viewDO = convert.toMaterializedViewDO(view);
        mapper.updateById(viewDO);
        return findById(IdUtil.toString(viewDO.getId()));
    }

    @Override
    public void deleteById(String id) {
        mapper.deleteById(IdUtil.toLong(id));
    }
}
