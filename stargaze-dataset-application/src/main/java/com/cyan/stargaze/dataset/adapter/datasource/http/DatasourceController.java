package com.cyan.stargaze.dataset.adapter.datasource.http;

import com.cyan.arch.common.api.Response;
import com.cyan.employee.client.dto.EmployeeDTO;
import com.cyan.employee.login.filter.UserContextHolder;
import com.cyan.stargaze.dataset.adapter.datasource.http.convert.DatasourceAdapterConvert;
import com.cyan.stargaze.dataset.adapter.datasource.http.dto.DatabaseDTO;
import com.cyan.stargaze.dataset.adapter.datasource.http.dto.DatasourceDTO;
import com.cyan.stargaze.dataset.adapter.datasource.http.dto.TableSampleDTO;
import com.cyan.stargaze.dataset.adapter.datasource.http.dto.TableSchemaDTO;
import com.cyan.stargaze.dataset.application.datasource.DatasourceService;
import com.cyan.stargaze.dataset.application.datasource.bo.DatasourceBO;
import com.cyan.stargaze.dataset.application.datasource.cmd.DatasourceCmd;
import com.cyan.stargaze.dataset.domain.datasource.query.DataSourceListQuery;
import com.cyan.stargaze.dataset.domain.datasource.valobj.DatabaseValObj;
import com.cyan.stargaze.dataset.domain.datasource.valobj.TableSampleValObj;
import com.cyan.stargaze.dataset.domain.datasource.valobj.TableSchemaValObj;
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
import java.util.Optional;

/**
 * 数据源控制器(/api)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/datasources")
public class DatasourceController {

    private final DatasourceService datasourceService;

    public DatasourceController(DatasourceService datasourceService) {
        this.datasourceService = datasourceService;
    }

    /**
     * 创建数据源
     */
    @PostMapping
    public Response<DatasourceDTO> create(@RequestBody @Valid DatasourceCmd cmd) {
        fillCurrentUser(cmd);
        DatasourceBO bo = datasourceService.create(cmd);
        return Response.success(DatasourceAdapterConvert.INSTANCE.toDatasourceDTO(bo));
    }

    /**
     * 更新数据源
     */
    @PutMapping("/{id}")
    public Response<DatasourceDTO> update(@PathVariable("id") String id, @RequestBody @Valid DatasourceCmd cmd) {
        cmd.setId(id);
        fillCurrentUser(cmd);
        DatasourceBO bo = datasourceService.update(cmd);
        return Response.success(DatasourceAdapterConvert.INSTANCE.toDatasourceDTO(bo));
    }

    /**
     * 列表
     */
    @GetMapping
    public Response<List<DatasourceDTO>> list(@RequestParam(value = "name", required = false) String name) {
        DataSourceListQuery query = new DataSourceListQuery().setName(name);
        List<DatasourceDTO> dtos = datasourceService.list(query).stream()
                .map(DatasourceAdapterConvert.INSTANCE::toDatasourceDTO)
                .toList();
        return Response.success(dtos);
    }

    /**
     * 详情
     */
    @GetMapping("/{id}")
    public Response<DatasourceDTO> findById(@PathVariable("id") String id) {
        DatasourceBO bo = datasourceService.findById(id);
        return Response.success(DatasourceAdapterConvert.INSTANCE.toDatasourceDTO(bo));
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public Response<Void> delete(@PathVariable("id") String id) {
        datasourceService.delete(id);
        return Response.success();
    }

    /**
     * 测试连接
     */
    @PostMapping("/{id}/test")
    public Response<Void> testConnection(@PathVariable("id") String id) {
        datasourceService.testConnection(id);
        return Response.success();
    }

    /**
     * 探查:库/schema 列表
     */
    @GetMapping("/{id}/schemas")
    public Response<List<DatabaseDTO>> listSchemas(@PathVariable("id") String id) {
        List<DatabaseDTO> dtos = Optional.ofNullable(datasourceService.listSchemas(id)).orElse(List.of()).stream()
                .map(DatasourceAdapterConvert.INSTANCE::toDatabaseDTO)
                .toList();
        return Response.success(dtos);
    }

    /**
     * 探查:表列表
     */
    @GetMapping("/{id}/schemas/{schema}/tables")
    public Response<List<String>> listTables(@PathVariable("id") String id,
                                             @PathVariable("schema") String schema) {
        return Response.success(datasourceService.listTables(id, schema));
    }

    /**
     * 探查:表结构
     */
    @GetMapping("/{id}/schemas/{schema}/tables/{table}")
    public Response<TableSchemaDTO> describeTable(@PathVariable("id") String id,
                                                  @PathVariable("schema") String schema,
                                                  @PathVariable("table") String table) {
        TableSchemaValObj valObj = datasourceService.describeTable(id, schema, table);
        return Response.success(DatasourceAdapterConvert.INSTANCE.toTableSchemaDTO(valObj));
    }

    /**
     * 探查:表采样
     */
    @GetMapping("/{id}/schemas/{schema}/tables/{table}/sample")
    public Response<TableSampleDTO> sampleTable(@PathVariable("id") String id,
                                                @PathVariable("schema") String schema,
                                                @PathVariable("table") String table,
                                                @RequestParam(value = "limit", defaultValue = "100") int limit) {
        TableSampleValObj valObj = datasourceService.sampleTable(id, schema, table, limit);
        return Response.success(DatasourceAdapterConvert.INSTANCE.toTableSampleDTO(valObj));
    }

    /**
     * 从登录上下文填充创建人/修改人,透传给应用层
     */
    private void fillCurrentUser(DatasourceCmd cmd) {
        EmployeeDTO employee = UserContextHolder.getCurrentEmployee();
        if (employee != null && employee.getId() != null) {
            cmd.setCreatedBy(employee.getId());
            cmd.setUpdatedBy(employee.getId());
        }
    }
}
