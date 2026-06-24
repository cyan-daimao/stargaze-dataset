package com.cyan.stargaze.dataset.application.dataset;

import com.cyan.stargaze.dataset.application.dataset.bo.DatasetFieldBO;
import com.cyan.stargaze.dataset.application.dataset.cmd.DatasetCreateCmd;

import java.util.List;

/**
 * 数据集来源字段解析服务:create 时 fields 为空自动推断。
 *
 * @author cy.Y
 * @since 1.0.0
 */
public interface DatasetResolveService {

    /**
     * 解析来源字段(按 create cmd 的 sourceType + config 分发)
     */
    List<DatasetFieldBO> resolveFields(DatasetCreateCmd cmd);
}
