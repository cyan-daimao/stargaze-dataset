package com.cyan.stargaze.dataset.adapter.dataset.rpc;

import com.cyan.arch.common.api.Page;
import com.cyan.arch.common.api.Response;
import com.cyan.stargaze.dataset.adapter.dataset.http.convert.DatasetAdapterConvert;
import com.cyan.stargaze.dataset.application.dataset.DatasetService;
import com.cyan.stargaze.dataset.application.dataset.bo.DatasetFieldBO;
import com.cyan.stargaze.dataset.application.dataset.bo.DatasetListBO;
import com.cyan.stargaze.dataset.client.DatasetClient;
import com.cyan.stargaze.dataset.client.dto.DatasetFieldDTO;
import com.cyan.stargaze.dataset.client.dto.DatasetListItemDTO;
import com.cyan.stargaze.dataset.client.dto.ResolveFieldDTO;
import com.cyan.stargaze.dataset.domain.dataset.query.DatasetListQuery;
import com.cyan.stargaze.dataset.enums.DatasetSourceType;
import com.cyan.stargaze.dataset.enums.DatasetStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
@RequestMapping("/rpc/v1/dataset")
@RequiredArgsConstructor
public class DatasetRpcController implements DatasetClient {

    private final DatasetService datasetService;

    @Override
    public Response<Page<DatasetListItemDTO>> page(
            @RequestParam(value = "workspaceId", required = false) String workspaceId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "sourceType", required = false) String sourceType,
            @RequestParam(value = "status", required = false) String status) {
        DatasetListQuery query = new DatasetListQuery()
                .setWorkspaceId(workspaceId)
                .setPage(page)
                .setSize(size)
                .setKeyword(keyword)
                .setSourceType(parseSourceType(sourceType))
                .setStatus(parseStatus(status));
        Page<DatasetListBO> p = datasetService.page(query);
        List<DatasetListItemDTO> list = p.getData().stream().map(this::toClientListItem).toList();
        return Response.success(new Page<DatasetListItemDTO>(list, p.getCurrent(), p.getSize(), p.getTotal()));
    }

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
        dto.setDatasourceId(datasetService.findById(datasetId).getDatasourceId());
        return Response.success(dto);
    }

    @Override
    public Response<Boolean> exists(@PathVariable("datasetId") String datasetId) {
        return Response.success(datasetService.exists(datasetId));
    }

    private DatasetListItemDTO toClientListItem(DatasetListBO bo) {
        return new DatasetListItemDTO()
                .setId(bo.getId())
                .setName(bo.getName())
                .setDescription(bo.getDescription())
                .setSourceType(bo.getSourceType() == null ? null : bo.getSourceType().name())
                .setSourceTypeName(bo.getSourceTypeName())
                .setDatasourceId(bo.getDatasourceId())
                .setDatasourceName(bo.getDatasourceName())
                .setStatus(bo.getStatus() == null ? null : bo.getStatus().name())
                .setFieldCount(bo.getFieldCount())
                .setDimensionCount(bo.getDimensionCount())
                .setMeasureCount(bo.getMeasureCount())
                .setVersion(bo.getVersion())
                .setCreatedBy(bo.getCreatedBy())
                .setCreator(bo.getCreator())
                .setCreatedAt(bo.getCreatedAt())
                .setUpdatedAt(bo.getUpdatedAt());
    }

    private DatasetSourceType parseSourceType(String sourceType) {
        if (sourceType == null || sourceType.isBlank()) {
            return null;
        }
        try {
            return DatasetSourceType.valueOf(sourceType.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private DatasetStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        try {
            return DatasetStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
