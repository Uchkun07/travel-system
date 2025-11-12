package io.github.uchkun07.travelsystem.service;

import io.github.uchkun07.travelsystem.entity.AdminPermission;

import java.util.List;

public interface IRolePermissionService {

    void bindPermissionsToRole(Integer roleId, List<Integer> permissionIds);

    void unbindPermissionsFromRole(Integer roleId, List<Integer> permissionIds);

    void unbindAllPermissionsFromRole(Integer roleId);

    List<AdminPermission> getRolePermissions(Integer roleId);
}
