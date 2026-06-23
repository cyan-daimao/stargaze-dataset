package com.cyan.stargaze.dataset.adapter.dataset.http;

import com.cyan.arch.common.api.Response;
import com.cyan.employee.client.dto.EmployeeDTO;
import com.cyan.employee.login.filter.UserContextHolder;
import com.cyan.stargaze.dataset.adapter.dataset.http.convert.DatasetAdapterConvert;
import com.cyan.stargaze.dataset.adapter.dataset.http.dto.DatasetDTO;
import com.cyan.stargaze.dataset.adapter.datasource.http.dto.TableSampleDTO;
import com.cyan.stargaze.dataset.application.dataset.DatasetHierarchyService;
import com.cyan.stargaze.dataset.application.dataset.DatasetParameterService;
import com.cyan.stargaze.dataset.application.dataset.DatasetService;
import com.cyan.stargaze.dataset.application.dataset.MaterializedViewService;
import com.cyan.stargaze.dataset.application.dataset.bo.DatasetBO;
import com.cyan.stargaze.dataset.application.dataset.cmd.DatasetCmd;
import com.cyan.stargaze.dataset.application.dataset.cmd.DatasetHierarchyCmd;
import com.cyan.stargaze.dataset.application.dataset.cmd.DatasetParameterCmd;
import com.cyan.stargaze.dataset.application.dataset.cmd.MaterializedViewCmd;
import com.cyan.stargaze.dataset.domain.datasource.valobj.TableSampleValObj;
import com.cyan.stargaze.dataset.domain.dataset.DatasetHierarchy;
import com.cyan.stargaze.dataset.domain.dataset.DatasetParameter;
import com.cyan.stargaze.dataset.domain.dataset.MaterializedView;
import com.cyan.stargaze.dataset.domain.dataset.query.DatasetListQuery;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 数据集控制器(/api)。
 * <p>
 * 数据集 CRUD、字段管理、元数据刷新、预览;辅助:层级/参数/物化加速配置。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/datasets")
public class DatasetController {

    private final DatasetService datasetService;
    private final DatasetHierarchyService hierarchyService;
    private final DatasetParameterService parameterService;
    private final MaterializedViewService materializedViewService;

    public DatasetController(DatasetService datasetService,
                             DatasetHierarchyService hierarchyService,
                             DatasetParameterService parameterService,
                             MaterializedViewService materializedViewService) {
        this.datasetService = datasetService;
        this.hierarchyService = hierarchyService;
        this.parameterService = parameterService;
        this.materializedViewService = materializedViewService;
    }

    // ==================== 数据集管理 ====================

    /**
     * 创建数据集
     */
    @PostMapping
    public Response<DatasetDTO> create(@RequestBody @Valid DatasetCmd cmd) {
        fillCurrentUser(cmd);
        DatasetBO bo = datasetService.create(cmd);
        return Response.success(DatasetAdapterConvert.INSTANCE.toDatasetDTO(bo));
    }

    /**
     * 更新数据集
     */
    @PutMapping("/{id}")
    public Response<DatasetDTO> update(@PathVariable("id") String id, @RequestBody @Valid DatasetCmd cmd) {
        cmd.setId(id);
        fillCurrentUser(cmd);
        DatasetBO bo = datasetService.update(cmd);
        return Response.success(DatasetAdapterConvert.INSTANCE.toDatasetDTO(bo));
    }

    /**
     * 列表
     */
    @GetMapping
    public Response<List<DatasetDTO>> list(@RequestParam(value = "workspaceId", required = false) String workspaceId,
                                           @RequestParam(value = "name", required = false) String name) {
        DatasetListQuery query = new DatasetListQuery().setWorkspaceId(workspaceId).setName(name);
        List<DatasetDTO> dtos = datasetService.list(query).stream()
                .map(DatasetAdapterConvert.INSTANCE::toDatasetDTO)
                .toList();
        return Response.success(dtos);
    }

    /**
     * 详情(含字段)
     */
    @GetMapping("/{id}")
    public Response<DatasetDTO> findById(@PathVariable("id") String id) {
        DatasetBO bo = datasetService.findById(id);
        return Response.success(DatasetAdapterConvert.INSTANCE.toDatasetDTO(bo));
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public Response<Void> delete(@PathVariable("id") String id) {
        datasetService.delete(id);
        return Response.success();
    }

    /**
     * 元数据刷新(重新采集表结构)
     */
    @PostMapping("/{id}/refresh")
    public Response<DatasetDTO> refresh(@PathVariable("id") String id) {
        DatasetBO bo = datasetService.refresh(id);
        return Response.success(DatasetAdapterConvert.INSTANCE.toDatasetDTO(bo));
    }

    /**
     * 预览数据(采样)
     */
    @GetMapping("/{id}/preview")
    public Response<TableSampleDTO> preview(@PathVariable("id") String id,
                                            @RequestParam(value = "limit", defaultValue = "100") int limit) {
        TableSampleValObj valObj = datasetService.preview(id, limit);
        return Response.success(toSampleDTO(valObj));
    }

    // ==================== 维度层级 ====================

    @PostMapping("/{id}/hierarchies")
    public Response<DatasetHierarchy> createHierarchy(@PathVariable("id") String id,
                                                      @RequestBody @Valid DatasetHierarchyCmd cmd) {
        cmd.setDatasetId(id);
        return Response.success(hierarchyService.create(cmd));
    }

    @GetMapping("/{id}/hierarchies")
    public Response<List<DatasetHierarchy>> listHierarchies(@PathVariable("id") String id) {
        return Response.success(hierarchyService.listByDatasetId(id));
    }

    @DeleteMapping("/hierarchies/{hierarchyId}")
    public Response<Void> deleteHierarchy(@PathVariable("hierarchyId") String hierarchyId) {
        hierarchyService.delete(hierarchyId);
        return Response.success();
    }

    // ==================== 参数字段 ====================

    @PostMapping("/{id}/parameters")
    public Response<DatasetParameter> createParameter(@PathVariable("id") String id,
                                                      @RequestBody @Valid DatasetParameterCmd cmd) {
        cmd.setDatasetId(id);
        return Response.success(parameterService.create(cmd));
    }

    @GetMapping("/{id}/parameters")
    public Response<List<DatasetParameter>> listParameters(@PathVariable("id") String id) {
        return Response.success(parameterService.listByDatasetId(id));
    }

    @DeleteMapping("/parameters/{parameterId}")
    public Response<Void> deleteParameter(@PathVariable("parameterId") String parameterId) {
        parameterService.delete(parameterId);
        return Response.success();
    }

    // ==================== 物化加速配置 ====================

    @PostMapping("/{id}/materialized-views")
    public Response<MaterializedView> createMaterializedView(@PathVariable("id") String id,
                                                             @RequestBody @Valid MaterializedViewCmd cmd) {
        cmd.setDatasetId(id);
        return Response.success(materializedViewService.create(cmd));
    }

    @GetMapping("/{id}/materialized-views")
    public Response<List<MaterializedView>> listMaterializedViews(@PathVariable("id") String id) {
        return Response.success(materializedViewService.listByDatasetId(id));
    }

    @DeleteMapping("/materialized-views/{viewId}")
    public Response<Void> deleteMaterializedView(@PathVariable("viewId") String viewId) {
        materializedViewService.delete(viewId);
        return Response.success();
    }

    // ==================== 私有方法 ====================

    private void fillCurrentUser(DatasetCmd cmd) {
        EmployeeDTO employee = UserContextHolder.getCurrentEmployee();
        if (employee != null && employee.getId() != null) {
            cmd.setCreatedBy(employee.getId());
            cmd.setUpdatedBy(employee.getId());
        }
    }

    private TableSampleDTO toSampleDTO(TableSampleValObj valObj) {
        return new TableSampleDTO().setColumns(valObj.getColumns()).setRows(valObj.getRows());
    }
}
