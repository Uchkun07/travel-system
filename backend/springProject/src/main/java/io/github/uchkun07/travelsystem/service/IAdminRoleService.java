package io.github.uchkun07.travelsystem.service;

import io.github.uchkun07.travelsystem.dto.AdminRoleRequest;
import io.github.uchkun07.travelsystem.dto.PageResponse;
import io.github.uchkun07.travelsystem.dto.RoleQueryRequest;
import io.github.uchkun07.travelsystem.entity.AdminRole;

import java.util.List;

public interface IAdminRoleService {

    AdminRole createRole(AdminRoleRequest request);

    void deleteRole(Integer roleId);

    void batchDeleteRoles(List<Integer> roleIds);

    AdminRole updateRole(AdminRoleRequest request);

    PageResponse<AdminRole> queryRoles(RoleQueryRequest request);

    AdminRole getRoleById(Integer roleId);

    List<AdminRole> getAllRoles();
}
