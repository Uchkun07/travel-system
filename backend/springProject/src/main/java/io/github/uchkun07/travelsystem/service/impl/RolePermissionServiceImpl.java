package io.github.uchkun07.travelsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.uchkun07.travelsystem.entity.AdminPermission;
import io.github.uchkun07.travelsystem.entity.AdminRolePermission;
import io.github.uchkun07.travelsystem.mapper.AdminPermissionMapper;
import io.github.uchkun07.travelsystem.mapper.AdminRolePermissionMapper;
import io.github.uchkun07.travelsystem.service.IRolePermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RolePermissionServiceImpl implements IRolePermissionService {

    @Autowired
    private AdminRolePermissionMapper rolePermissionMapper;

    @Autowired
    private AdminPermissionMapper permissionMapper;

    @Override
    @Transactional
    public void bindPermissionsToRole(Integer roleId, List<Integer> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            throw new IllegalArgumentException("权限ID列表不能为空");
        }

        for (Integer permissionId : permissionIds) {
            LambdaQueryWrapper<AdminRolePermission> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AdminRolePermission::getRoleId, roleId)
                   .eq(AdminRolePermission::getPermissionId, permissionId);
            
            if (rolePermissionMapper.selectCount(wrapper) == 0) {
                AdminRolePermission relation = AdminRolePermission.builder()
                        .roleId(roleId)
                        .permissionId(permissionId)
                        .build();
                rolePermissionMapper.insert(relation);
            }
        }
        log.info("角色绑定权限成功: roleId={}, permissionIds={}", roleId, permissionIds);
    }

    @Override
    @Transactional
    public void unbindPermissionsFromRole(Integer roleId, List<Integer> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            throw new IllegalArgumentException("权限ID列表不能为空");
        }

        for (Integer permissionId : permissionIds) {
            LambdaQueryWrapper<AdminRolePermission> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AdminRolePermission::getRoleId, roleId)
                   .eq(AdminRolePermission::getPermissionId, permissionId);
            rolePermissionMapper.delete(wrapper);
        }
        log.info("角色解绑权限成功: roleId={}, permissionIds={}", roleId, permissionIds);
    }

    @Override
    @Transactional
    public void unbindAllPermissionsFromRole(Integer roleId) {
        LambdaQueryWrapper<AdminRolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminRolePermission::getRoleId, roleId);
        rolePermissionMapper.delete(wrapper);
        log.info("角色解绑所有权限成功: roleId={}", roleId);
    }

    @Override
    public List<AdminPermission> getRolePermissions(Integer roleId) {
        LambdaQueryWrapper<AdminRolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminRolePermission::getRoleId, roleId);
        List<AdminRolePermission> relations = rolePermissionMapper.selectList(wrapper);

        if (relations.isEmpty()) {
            return new ArrayList<>();
        }

        List<Integer> permissionIds = relations.stream()
                .map(AdminRolePermission::getPermissionId)
                .collect(Collectors.toList());

        LambdaQueryWrapper<AdminPermission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(AdminPermission::getPermissionId, permissionIds);
        return permissionMapper.selectList(queryWrapper);
    }
}
