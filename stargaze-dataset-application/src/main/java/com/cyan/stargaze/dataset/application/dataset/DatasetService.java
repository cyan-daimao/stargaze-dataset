package com.cyan.stargaze.dataset.application.dataset;

import com.cyan.stargaze.dataset.application.dataset.bo.DatasetBO;
import com.cyan.stargaze.dataset.application.dataset.bo.DatasetFieldBO;
import com.cyan.stargaze.dataset.application.dataset.cmd.DatasetCmd;
import com.cyan.stargaze.dataset.domain.datasource.valobj.TableSampleValObj;
import com.cyan.stargaze.dataset.domain.datasource.valobj.TableSchemaValObj;
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
    DatasetBO create(DatasetCmd cmd);

    /**
     * 更新数据集(全量重建字段)
     */
    DatasetBO update(DatasetCmd cmd);

    /**
     * 列表查询
     */
    List<DatasetBO> list(DatasetListQuery query);

    /**
     * 详情(含字段)
     */
    DatasetBO findById(String id);

    /**
     * 删除
     */
    void delete(String id);

    /**
     * 元数据刷新(重新采集表结构与采样)
     */
    DatasetBO refresh(String id);

    /**
     * 预览数据(采样 100 行)
     */
    TableSampleValObj preview(String id, int limit);

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
     * 根据数据集 ID 探查其源表结构(刷新用)
     */
    TableSchemaValObj describeSourceTable(String datasetId);
}
