package com.cyan.stargaze.dataset.application.dataset.support;

import com.cyan.arch.common.api.Assert;
import com.cyan.arch.common.api.Response;
import com.cyan.employee.client.EmployeeClient;
import com.cyan.employee.client.dto.EmployeeDTO;
import com.cyan.employee.client.query.EmployeeRPCListQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 员工名称解析器:批量按员工 ID 查询中文名(列表/详情 creator 名组装用)。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class EmployeeNameResolver {

    private final EmployeeClient employeeClient;

    /**
     * 批量查询员工 ID -> 中文名映射。
     */
    public Map<String, String> resolveNames(List<String> employeeIds) {
        Map<String, String> result = new HashMap<>();
        if (employeeIds == null || employeeIds.isEmpty()) {
            return result;
        }
        Response<List<EmployeeDTO>> resp = employeeClient.list(new EmployeeRPCListQuery(employeeIds, null));
        Assert.notNull(resp, new com.cyan.arch.common.api.SilentException("员工服务无响应"));
        List<EmployeeDTO> employees = resp.getData();
        if (employees == null) {
            return result;
        }
        for (EmployeeDTO e : employees) {
            if (e != null && e.getId() != null) {
                result.put(e.getId(), e.getCnName());
            }
        }
        return result;
    }
}
