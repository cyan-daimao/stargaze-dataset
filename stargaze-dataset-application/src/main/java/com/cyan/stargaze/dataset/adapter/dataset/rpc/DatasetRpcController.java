package com.cyan.stargaze.dataset.adapter.dataset.rpc;

import com.cyan.arch.common.api.Response;
import com.cyan.stargaze.dataset.adapter.dataset.http.convert.DatasetAdapterConvert;
import com.cyan.stargaze.dataset.application.dataset.DatasetService;
import com.cyan.stargaze.dataset.application.dataset.bo.DatasetFieldBO;
import com.cyan.stargaze.dataset.client.DatasetClient;
import com.cyan.stargaze.dataset.client.dto.DatasetFieldDTO;
import com.cyan.stargaze.dataset.client.dto.ResolveFieldDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 数据集 RPC 控制器:实现 {@link DatasetClient} 契约(/rpc/v1/dataset)。
 * <p>
 * 供 metric/query 等服务依赖调用,不依赖登录态。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@RestController
@RequiredArgsConstructor
public class DatasetRpcController implements DatasetClient {

    private final DatasetService datasetService;

    @Override
    public Response<List<DatasetFieldDTO>> listFields(@PathVariable("datasetId") String datasetId) {
        List<DatasetFieldBO> fields = datasetService.listFields(datasetId);
        return Response.success(DatasetAdapterConvert.INSTANCE.toClientDatasetFieldDTOList(fields));
    }

    @Override
    public Response<ResolveFieldDTO> resolveField(@PathVariable("datasetId") String datasetId,
                                                  @PathVariable("fieldId") String fieldId) {
        DatasetFieldBO field = datasetService.resolveField(datasetId, fieldId);
        ResolveFieldDTO dto = DatasetAdapterConvert.INSTANCE.toResolveFieldDTO(field);
        // 补充数据源 ID(供 metric binding 推断)
        dto.setDatasourceId(datasetService.findById(datasetId).getDataSourceId());
        return Response.success(dto);
    }

    @Override
    public Response<Boolean> exists(@PathVariable("datasetId") String datasetId) {
        return Response.success(datasetService.exists(datasetId));
    }
}
