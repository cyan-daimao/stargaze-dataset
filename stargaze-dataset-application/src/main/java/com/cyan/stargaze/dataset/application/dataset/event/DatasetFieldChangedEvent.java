package com.cyan.stargaze.dataset.application.dataset.event;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * 数据集字段变更事件(刷新后发布,供 metric 订阅做口径校验)。
 * <p>
 * 一期为进程内事件,二期可接 MQ。
 *
 * @param datasetId  数据集 ID
 * @param added      新增字段 originName 列表
 * @param removed    删除字段 originName 列表
 * @param changed    类型变更字段 originName 列表
 * @param occurredAt 发生时间
 * @author cy.Y
 * @since 1.0.0
 */
public record DatasetFieldChangedEvent(String datasetId,
                                       List<String> added, List<String> removed, List<String> changed,
                                       OffsetDateTime occurredAt) {
}
