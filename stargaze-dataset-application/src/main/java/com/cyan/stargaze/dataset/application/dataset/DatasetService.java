package com.cyan.stargaze.dataset.application.dataset;

import com.cyan.arch.common.api.Page;
import com.cyan.stargaze.dataset.application.dataset.bo.DatasetBO;
import com.cyan.stargaze.dataset.application.dataset.bo.DatasetFieldBO;
import com.cyan.stargaze.dataset.application.dataset.bo.DatasetListBO;
import com.cyan.stargaze.dataset.application.dataset.bo.DatasetQueryRouteBO;
import com.cyan.stargaze.dataset.application.dataset.bo.DatasetSyncBO;
import com.cyan.stargaze.dataset.application.dataset.bo.SqlPreviewBO;
import com.cyan.stargaze.dataset.application.dataset.cmd.DatasetCreateCmd;
import com.cyan.stargaze.dataset.application.dataset.cmd.DatasetSyncCmd;
import com.cyan.stargaze.dataset.application.dataset.cmd.DatasetUpdateCmd;
import com.cyan.stargaze.dataset.application.dataset.cmd.SqlPreviewCmd;
import com.cyan.stargaze.dataset.domain.dataset.query.DatasetListQuery;

import java.util.List;

/**
 * 数据集应用服务。
 *
 * @author cy.Y
 * @since 1.0.0
 */
public interface DatasetService {

    /**
     * 创建数据集(含字段)
     */
    DatasetBO create(DatasetCreateCmd cmd);

    /**
     * 更新数据集(名称/描述/字段,版本号递增)
     */
    DatasetBO update(String id, DatasetUpdateCmd cmd);

    /**
     * 分页查询(批量组装 datasource_name/字段统计/creator 名)
     */
    Page<DatasetListBO> page(DatasetListQuery query);

    /**
     * 详情(含 config/fields/statistics)
     */
    DatasetBO findById(String id);

    /**
     * 删除(逻辑删除)
     */
    void delete(String id);

    /**
     * SQL 预览(校验 + 解析列/类型 + 采样)
     */
    SqlPreviewBO sqlPreview(SqlPreviewCmd cmd);

    /**
     * 同步(一期占位:复用元数据刷新,返回 RUNNING)
     */
    DatasetSyncBO sync(String id, DatasetSyncCmd cmd);

    /**
     * 同步状态(一期占位:返回 SUCCESS)
     */
    DatasetSyncBO syncStatus(String id);

    // ==================== 供 RPC / 内部 ====================

    /**
     * 查询数据集全部字段(供 metric 绑定)
     */
    List<DatasetFieldBO> listFields(String datasetId);

    /**
     * 解析指定字段(校验 + 类型推断,供 metric binding)
     */
    DatasetFieldBO resolveField(String datasetId, String fieldId);

    /**
     * 数据集是否存在且可用
     */
    boolean exists(String datasetId);

    /**
     * 解析查询路由
     */
    DatasetQueryRouteBO queryRoute(String datasetId);

    /**
     * 元数据刷新(重新采集字段,内部供 sync 复用)
     */
    DatasetBO refresh(String id);

    /**
     * 预览数据(采样,内部供 statistics 复用)
     */
    com.cyan.stargaze.dataset.domain.datasource.valobj.TableSampleValObj preview(String id, int limit);
}
