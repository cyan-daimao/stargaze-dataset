package com.cyan.stargaze.dataset.application.dataset.scheduler;

import com.cyan.stargaze.dataset.application.dataset.DatasetService;
import com.cyan.stargaze.dataset.domain.dataset.Dataset;
import com.cyan.stargaze.dataset.domain.dataset.query.DatasetListQuery;
import com.cyan.stargaze.dataset.domain.dataset.repository.DatasetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 数据集元数据刷新调度器(服务内置轻量定时,不单设 schedule 服务)。
 * <p>
 * 每日凌晨自动同步结构变更:重新采集表结构,新增字段加入、删除字段标记失效,
 * 并触发 dataset.field.changed 通知 metric 校验 binding。
 * 生产环境应配合分布式锁避免多实例重复执行。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DatasetRefreshScheduler {

    private final DatasetRepository datasetRepository;
    private final DatasetService datasetService;

    /**
     * 每日 01:30 全量刷新数据集元数据(cron 由 dataset-refresh.cron 覆盖)
     */
    @Scheduled(cron = "${dataset-refresh.cron:0 30 1 * * ?}")
    public void refreshAll() {
        DatasetListQuery query = new DatasetListQuery().setPage(1).setSize(10000);
        com.cyan.arch.common.api.Page<Dataset> page = datasetRepository.page(query);
        List<Dataset> datasets = page.getData();
        log.info("数据集元数据刷新开始,共 {} 个数据集", datasets.size());
        int success = 0;
        for (Dataset dataset : datasets) {
            try {
                datasetService.refresh(dataset.getId());
                success++;
            } catch (Exception e) {
                // 单个数据集刷新失败不影响整体
                log.error("数据集元数据刷新失败, datasetId={}", dataset.getId(), e);
            }
        }
        log.info("数据集元数据刷新结束,成功 {}/{}", success, datasets.size());
    }
}
