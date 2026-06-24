package com.cyan.stargaze.dataset.application.dataset.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 数据集字段变更事件日志监听器(占位,预留 metric 服务订阅)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Slf4j
@Component
public class DatasetFieldChangedLogger {

    @EventListener
    public void onFieldChanged(DatasetFieldChangedEvent event) {
        log.info("数据集字段变更通知 dataset.field.changed, datasetId={}, workspaceId={}, added={}, removed={}, changed={}",
                event.datasetId(), event.workspaceId(),
                event.added(), event.removed(), event.changed());
    }
}
