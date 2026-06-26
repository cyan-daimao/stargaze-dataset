package com.cyan.stargaze.dataset.client;

import com.cyan.arch.common.api.Page;
import com.cyan.arch.common.api.Response;
import com.cyan.stargaze.dataset.client.dto.DatasetFieldDTO;
import com.cyan.stargaze.dataset.client.dto.DatasetListItemDTO;
import com.cyan.stargaze.dataset.client.dto.ResolveFieldDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 数据集平台 RPC 契约(服务间调用)。
 * <p>
 * 路径统一 /rpc/v1/dataset,不依赖登录态,供 metric/query 等服务依赖。
 * application 模块的 Controller 必须实现本接口。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@FeignClient(name = "stargaze-dataset", contextId = "datasetClient", path = "/rpc/v1/dataset", url = "${feign.stargaze-dataset.url:}")
public interface DatasetClient {

    /**
     * 分页查询数据集列表(供 metric 一键同步)
     *
     * @param workspaceId 空间 ID
     * @param page        页码
     * @param size        每页条数
     * @param keyword     关键词
     * @param sourceType  来源类型
     * @param status      状态
     * @return 分页结果
     */
    @GetMapping
    Response<Page<DatasetListItemDTO>> page(
            @RequestParam(value = "workspaceId", required = false) String workspaceId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "sourceType", required = false) String sourceType,
            @RequestParam(value = "status", required = false) String status);

    /**
     * 查询数据集全部字段(供 metric 绑定字段)
     *
     * @param datasetId 数据集 ID
     * @return 字段列表
     */
    @GetMapping("/{datasetId}/fields")
    Response<List<DatasetFieldDTO>> listFields(@PathVariable("datasetId") String datasetId);

    /**
     * 解析指定字段(校验存在性 + 类型推断,供 metric binding 使用)
     *
     * @param datasetId 数据集 ID
     * @param fieldId   字段 ID
     * @return 字段解析结果
     */
    @GetMapping("/{datasetId}/fields/{fieldId}/resolve")
    Response<ResolveFieldDTO> resolveField(@PathVariable("datasetId") String datasetId,
                                           @PathVariable("fieldId") String fieldId);

    /**
     * 校验数据集是否存在且可用
     *
     * @param datasetId 数据集 ID
     * @return 是否可用
     */
    @GetMapping("/{datasetId}/exists")
    Response<Boolean> exists(@PathVariable("datasetId") String datasetId);
}
