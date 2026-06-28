package com.cyan.stargaze.dataset.adapter.dataset.http;

import com.cyan.arch.common.api.Page;
import com.cyan.arch.common.api.Response;
import com.cyan.employee.client.dto.EmployeeDTO;
import com.cyan.employee.login.filter.UserContextHolder;
import com.cyan.stargaze.dataset.adapter.dataset.http.convert.DatasetAdapterConvert;
import com.cyan.stargaze.dataset.adapter.dataset.http.dto.DatasetCreateDTO;
import com.cyan.stargaze.dataset.adapter.dataset.http.dto.DatasetDeleteDTO;
import com.cyan.stargaze.dataset.adapter.dataset.http.dto.DatasetDetailDTO;
import com.cyan.stargaze.dataset.adapter.dataset.http.dto.DatasetHierarchyDTO;
import com.cyan.stargaze.dataset.adapter.dataset.http.dto.DatasetListDTO;
import com.cyan.stargaze.dataset.adapter.dataset.http.dto.DatasetParameterDTO;
import com.cyan.stargaze.dataset.adapter.dataset.http.dto.DatasetSyncDTO;
import com.cyan.stargaze.dataset.adapter.dataset.http.dto.DatasetSyncStatusDTO;
import com.cyan.stargaze.dataset.adapter.dataset.http.dto.DatasetUpdateDTO;
import com.cyan.stargaze.dataset.adapter.dataset.http.dto.ExcelPreviewDTO;
import com.cyan.stargaze.dataset.adapter.dataset.http.dto.ExcelUploadDTO;
import com.cyan.stargaze.dataset.adapter.dataset.http.dto.MaterializedViewDTO;
import com.cyan.stargaze.dataset.adapter.dataset.http.dto.SqlPreviewDTO;
import com.cyan.stargaze.dataset.adapter.dataset.http.dto.TableFieldsDTO;
import com.cyan.stargaze.dataset.adapter.dataset.http.dto.TableListDTO;
import com.cyan.stargaze.dataset.adapter.datasource.http.dto.DatabaseDTO;
import com.cyan.stargaze.dataset.adapter.datasource.http.convert.DatasourceAdapterConvert;
import com.cyan.stargaze.dataset.application.datasource.bo.DatasourceBO;
import com.cyan.stargaze.dataset.application.dataset.DatasetFileService;
import com.cyan.stargaze.dataset.application.dataset.DatasetHierarchyService;
import com.cyan.stargaze.dataset.application.dataset.DatasetParameterService;
import com.cyan.stargaze.dataset.application.dataset.DatasetService;
import com.cyan.stargaze.dataset.application.dataset.MaterializedViewService;
import com.cyan.stargaze.dataset.application.dataset.bo.DatasetBO;
import com.cyan.stargaze.dataset.application.dataset.bo.DatasetFileBO;
import com.cyan.stargaze.dataset.application.dataset.bo.DatasetQueryRouteBO;
import com.cyan.stargaze.dataset.application.dataset.bo.ExcelPreviewBO;
import com.cyan.stargaze.dataset.application.dataset.bo.ExcelSheetBO;
import com.cyan.stargaze.dataset.application.dataset.bo.SqlPreviewBO;
import com.cyan.stargaze.dataset.client.dto.DatasetQueryRouteDTO;
import com.cyan.stargaze.dataset.application.dataset.cmd.DatasetCreateCmd;
import com.cyan.stargaze.dataset.application.dataset.cmd.DatasetHierarchyCmd;
import com.cyan.stargaze.dataset.application.dataset.cmd.DatasetParameterCmd;
import com.cyan.stargaze.dataset.application.dataset.cmd.DatasetSyncCmd;
import com.cyan.stargaze.dataset.application.dataset.cmd.DatasetUpdateCmd;
import com.cyan.stargaze.dataset.application.dataset.cmd.MaterializedViewCmd;
import com.cyan.stargaze.dataset.application.dataset.cmd.SqlPreviewCmd;
import com.cyan.stargaze.dataset.application.datasource.DatasourceService;
import com.cyan.stargaze.dataset.domain.dataset.query.DatasetListQuery;
import com.cyan.stargaze.dataset.enums.DatasetSourceType;
import com.cyan.stargaze.dataset.enums.DatasetStatus;
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
import org.springframework.web.multipart.MultipartFile;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 数据集控制器(/api/v1/datasets)。
 * <p>
 * 数据集 CRUD、SQL 预览、表探查、Excel 上传/预览、同步;辅助:层级/参数/物化加速配置。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/datasets")
public class DatasetController {

    private final DatasetService datasetService;
    private final DatasourceService datasourceService;
    private final DatasetFileService datasetFileService;
    private final DatasetHierarchyService hierarchyService;
    private final DatasetParameterService parameterService;
    private final MaterializedViewService materializedViewService;

    public DatasetController(DatasetService datasetService,
                             DatasourceService datasourceService,
                             DatasetFileService datasetFileService,
                             DatasetHierarchyService hierarchyService,
                             DatasetParameterService parameterService,
                             MaterializedViewService materializedViewService) {
        this.datasetService = datasetService;
        this.datasourceService = datasourceService;
        this.datasetFileService = datasetFileService;
        this.hierarchyService = hierarchyService;
        this.parameterService = parameterService;
        this.materializedViewService = materializedViewService;
    }

    // ==================== 1. 列表 ====================

    @GetMapping
    public Response<Page<DatasetListDTO>> page(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "sourceType", required = false) DatasetSourceType sourceType,
            @RequestParam(value = "status", required = false) DatasetStatus status) {
        DatasetListQuery query = new DatasetListQuery()
                .setPage(page).setSize(size).setKeyword(keyword).setSourceType(sourceType).setStatus(status);
        Page<com.cyan.stargaze.dataset.application.dataset.bo.DatasetListBO> p = datasetService.page(query);
        List<DatasetListDTO> list = DatasetAdapterConvert.INSTANCE.toDatasetListDTOList(p.getData());
        return Response.success(new Page<>(list, p.getCurrent(), p.getSize(), p.getTotal()));
    }

    // ==================== 2. 创建 ====================

    @PostMapping
    public Response<DatasetCreateDTO> create(@RequestBody @Valid DatasetCreateCmd cmd) {
        fillCurrentUser(cmd);
        DatasetBO bo = datasetService.create(cmd);
        DatasetCreateDTO dto = new DatasetCreateDTO()
                .setId(bo.getId())
                .setName(bo.getName())
                .setStatus(bo.getStatus())
                .setCreatedAt(bo.getCreatedAt());
        return Response.success(dto);
    }

    // ==================== 3. 详情 ====================

    @GetMapping("/{id}")
    public Response<DatasetDetailDTO> findById(@PathVariable("id") String id) {
        DatasetBO bo = datasetService.findById(id);
        return Response.success(DatasetAdapterConvert.INSTANCE.toDatasetDetailDTO(bo));
    }

    // ==================== 4. 更新 ====================

    @PutMapping("/{id}")
    public Response<DatasetUpdateDTO> update(@PathVariable("id") String id, @RequestBody @Valid DatasetUpdateCmd cmd) {
        fillUpdatedBy(cmd);
        DatasetBO bo = datasetService.update(id, cmd);
        return Response.success(new DatasetUpdateDTO().setId(bo.getId())
                .setVersion(DatasetAdapterConvert.INSTANCE.versionLabel(bo.getVersion()))
                .setUpdatedAt(bo.getUpdatedAt()));
    }

    // ==================== 5. 删除 ====================

    @DeleteMapping("/{id}")
    public Response<DatasetDeleteDTO> delete(@PathVariable("id") String id) {
        datasetService.delete(id);
        return Response.success(new DatasetDeleteDTO().setId(id).setDeletedAt(OffsetDateTime.now()));
    }

    // ==================== 6. SQL 预览 ====================

    @PostMapping("/sql-preview")
    public Response<SqlPreviewDTO> sqlPreview(@RequestBody @Valid SqlPreviewCmd cmd) {
        SqlPreviewBO bo = datasetService.sqlPreview(cmd);
        return Response.success(DatasetAdapterConvert.INSTANCE.toSqlPreviewDTO(bo));
    }

    // ==================== 7. 表列表 ====================

    @GetMapping("/tables")
    public Response<TableListDTO> listTables(@RequestParam("datasourceId") String datasourceId,
                                             @RequestParam(value = "schema", required = false) String schema,
                                             @RequestParam(value = "keyword", required = false) String keyword) {
        boolean supportsSchema = datasourceService.supportsSchema(datasourceId);
        List<String> schemas;
        String effectiveSchema = schema;
        if (supportsSchema) {
            schemas = Optional.ofNullable(datasourceService.listSchemas(datasourceId)).orElse(List.of())
                    .stream().map(DatasourceAdapterConvert.INSTANCE::toDatabaseDTO)
                    .map(DatabaseDTO::getName).toList();
        } else {
            // MySQL/StarRocks/Doris/ClickHouse 等库级数据源:连接已指向具体 database,无需再选 schema
            schemas = List.of();
            if (effectiveSchema == null || effectiveSchema.isBlank()) {
                DatasourceBO ds = datasourceService.findById(datasourceId);
                effectiveSchema = ds.getConfig() == null ? null : ds.getConfig().getDatabase();
            }
        }
        List<com.cyan.stargaze.dataset.domain.datasource.valobj.TableMetaValObj> tables =
                datasourceService.listTablesRich(datasourceId, effectiveSchema, keyword);
        TableListDTO dto = new TableListDTO()
                .setSchemaSupported(supportsSchema)
                .setSchemas(schemas)
                .setTables(DatasetAdapterConvert.INSTANCE.toTableMetaDTOList(tables));
        return Response.success(dto);
    }

    // ==================== 8. 表字段 ====================

    @GetMapping("/tables/{schema}/{tableName}/fields")
    public Response<TableFieldsDTO> tableFields(@PathVariable("schema") String schema,
                                                @PathVariable("tableName") String tableName,
                                                @RequestParam("datasourceId") String datasourceId) {
        com.cyan.stargaze.dataset.domain.datasource.valobj.TableSchemaValObj valObj =
                datasourceService.describeTable(datasourceId, schema, tableName);
        return Response.success(DatasetAdapterConvert.INSTANCE.toTableFieldsDTO(valObj));
    }

    // ==================== 9. Excel 上传 ====================

    @PostMapping("/upload")
    public Response<ExcelUploadDTO> upload(@RequestParam("file") MultipartFile file) {
        DatasetFileBO fileBO = datasetFileService.upload(file);
        List<String> sheetNames = Optional.ofNullable(datasetFileService.listSheets(fileBO.getId()))
                .orElse(List.of()).stream()
                .map(ExcelSheetBO::getName)
                .toList();
        ExcelUploadDTO dto = new ExcelUploadDTO()
                .setFileId(fileBO.getId())
                .setFileName(fileBO.getFileName())
                .setFileSize(fileBO.getSize())
                .setSheetNames(sheetNames)
                .setPreviewUrl("/api/v1/datasets/preview/" + fileBO.getId());
        return Response.success(dto);
    }

    // ==================== 10. Excel 预览 ====================

    @GetMapping("/preview/{fileId}")
    public Response<ExcelPreviewDTO> excelPreview(@PathVariable("fileId") String fileId,
                                                  @RequestParam(value = "sheetName", required = false) String sheetName,
                                                  @RequestParam(value = "limit", defaultValue = "5") int limit) {
        // sheetName 为空取第一个 sheet
        if (sheetName == null || sheetName.isBlank()) {
            List<ExcelSheetBO> sheets = datasetFileService.listSheets(fileId);
            if (sheets == null || sheets.isEmpty()) {
                throw new com.cyan.arch.common.api.SilentException("文件无可用工作表");
            }
            sheetName = sheets.get(0).getName();
        }
        ExcelPreviewBO bo = datasetFileService.preview(fileId, sheetName, 1, limit);
        return Response.success(DatasetAdapterConvert.INSTANCE.toExcelPreviewDTO(bo));
    }

    // ==================== 11. 同步 ====================

    @PostMapping("/{id}/sync")
    public Response<DatasetSyncDTO> sync(@PathVariable("id") String id, @RequestBody(required = false) DatasetSyncCmd cmd) {
        return Response.success(DatasetAdapterConvert.INSTANCE.toDatasetSyncDTO(datasetService.sync(id, cmd)));
    }

    // ==================== 12. 同步状态 ====================

    @GetMapping("/{id}/sync-status")
    public Response<DatasetSyncStatusDTO> syncStatus(@PathVariable("id") String id) {
        return Response.success(DatasetAdapterConvert.INSTANCE.toDatasetSyncStatusDTO(datasetService.syncStatus(id)));
    }

    // ==================== 13. 查询路由 ====================

    @GetMapping("/{id}/query-route")
    public Response<DatasetQueryRouteDTO> queryRoute(@PathVariable("id") String id) {
        DatasetQueryRouteBO bo = datasetService.queryRoute(id);
        return Response.success(new DatasetQueryRouteDTO()
                .setDatasetId(bo.getDatasetId())
                .setExecutionMode(bo.getExecutionMode())
                .setEngine(bo.getEngine())
                .setTableRef(bo.getTableRef())
                .setCatalogName(bo.getCatalogName())
                .setDatabaseName(bo.getDatabaseName())
                .setSchemaName(bo.getSchemaName())
                .setTableName(bo.getTableName())
                .setSyncStatus(bo.getSyncStatus())
                .setLastSyncAt(bo.getLastSyncAt())
                .setLastError(bo.getLastError())
                .setFieldMappings(bo.getFieldMappings()));
    }

    // ==================== 维度层级 ====================

    @PostMapping("/{id}/hierarchies")
    public Response<DatasetHierarchyDTO> createHierarchy(@PathVariable("id") String id,
                                                         @RequestBody @Valid DatasetHierarchyCmd cmd) {
        cmd.setDatasetId(id);
        return Response.success(DatasetAdapterConvert.INSTANCE.toDatasetHierarchyDTO(hierarchyService.create(cmd)));
    }

    @GetMapping("/{id}/hierarchies")
    public Response<List<DatasetHierarchyDTO>> listHierarchies(@PathVariable("id") String id) {
        return Response.success(DatasetAdapterConvert.INSTANCE.toDatasetHierarchyDTOList(hierarchyService.listByDatasetId(id)));
    }

    @DeleteMapping("/hierarchies/{hierarchyId}")
    public Response<Void> deleteHierarchy(@PathVariable("hierarchyId") String hierarchyId) {
        hierarchyService.delete(hierarchyId);
        return Response.success();
    }

    // ==================== 参数字段 ====================

    @PostMapping("/{id}/parameters")
    public Response<DatasetParameterDTO> createParameter(@PathVariable("id") String id,
                                                         @RequestBody @Valid DatasetParameterCmd cmd) {
        cmd.setDatasetId(id);
        return Response.success(DatasetAdapterConvert.INSTANCE.toDatasetParameterDTO(parameterService.create(cmd)));
    }

    @GetMapping("/{id}/parameters")
    public Response<List<DatasetParameterDTO>> listParameters(@PathVariable("id") String id) {
        return Response.success(DatasetAdapterConvert.INSTANCE.toDatasetParameterDTOList(parameterService.listByDatasetId(id)));
    }

    @DeleteMapping("/parameters/{parameterId}")
    public Response<Void> deleteParameter(@PathVariable("parameterId") String parameterId) {
        parameterService.delete(parameterId);
        return Response.success();
    }

    // ==================== 物化加速配置 ====================

    @PostMapping("/{id}/materialized-views")
    public Response<MaterializedViewDTO> createMaterializedView(@PathVariable("id") String id,
                                                                @RequestBody @Valid MaterializedViewCmd cmd) {
        cmd.setDatasetId(id);
        return Response.success(DatasetAdapterConvert.INSTANCE.toMaterializedViewDTO(materializedViewService.create(cmd)));
    }

    @GetMapping("/{id}/materialized-views")
    public Response<List<MaterializedViewDTO>> listMaterializedViews(@PathVariable("id") String id) {
        return Response.success(DatasetAdapterConvert.INSTANCE.toMaterializedViewDTOList(materializedViewService.listByDatasetId(id)));
    }

    @DeleteMapping("/materialized-views/{viewId}")
    public Response<Void> deleteMaterializedView(@PathVariable("viewId") String viewId) {
        materializedViewService.delete(viewId);
        return Response.success();
    }

    // ==================== 私有 ====================

    private void fillCurrentUser(DatasetCreateCmd cmd) {
        EmployeeDTO employee = UserContextHolder.getCurrentEmployee();
        if (employee != null && employee.getId() != null) {
            cmd.setCreatedBy(employee.getId());
        }
    }

    private void fillUpdatedBy(DatasetUpdateCmd cmd) {
        EmployeeDTO employee = UserContextHolder.getCurrentEmployee();
        if (employee != null && employee.getId() != null) {
            cmd.setUpdatedBy(employee.getId());
        }
    }

    private String currentUserId() {
        EmployeeDTO employee = UserContextHolder.getCurrentEmployee();
        return employee == null ? null : employee.getId();
    }
}
