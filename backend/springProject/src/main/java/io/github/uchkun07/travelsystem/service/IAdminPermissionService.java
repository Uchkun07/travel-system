package io.github.uchkun07.travelsystem.service;

import io.github.uchkun07.travelsystem.dto.AdminPermissionRequest;
import io.github.uchkun07.travelsystem.dto.PageResponse;
import io.github.uchkun07.travelsystem.dto.PermissionQueryRequest;
import io.github.uchkun07.travelsystem.entity.AdminPermission;

import java.util.List;

public interface IAdminPermissionService {

    AdminPermission createPermission(AdminPermissionRequest request);

    void deletePermission(Integer permissionId);

    void batchDeletePermissions(List<Integer> permissionIds);

    AdminPermission updatePermission(AdminPermissionRequest request);

    PageResponse<AdminPermission> queryPermissions(PermissionQueryRequest request);

    AdminPermission getPermissionById(Integer permissionId);

    List<AdminPermission> getAllPermissions();
}
