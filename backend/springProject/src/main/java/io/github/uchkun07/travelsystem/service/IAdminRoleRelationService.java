package io.github.uchkun07.travelsystem.service;

import io.github.uchkun07.travelsystem.entity.AdminRole;

import java.util.List;

public interface IAdminRoleRelationService {

    void bindRolesToAdmin(Long adminId, List<Integer> roleIds);

    void unbindRolesFromAdmin(Long adminId, List<Integer> roleIds);

    void unbindAllRolesFromAdmin(Long adminId);

    List<AdminRole> getAdminRoles(Long adminId);
}
