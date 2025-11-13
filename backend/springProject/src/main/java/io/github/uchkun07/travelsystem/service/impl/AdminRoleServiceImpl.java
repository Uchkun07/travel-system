package io.github.uchkun07.travelsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.uchkun07.travelsystem.dto.AdminRoleRequest;
import io.github.uchkun07.travelsystem.dto.PageResponse;
import io.github.uchkun07.travelsystem.dto.RoleQueryRequest;
import io.github.uchkun07.travelsystem.entity.AdminRole;
import io.github.uchkun07.travelsystem.mapper.AdminRoleMapper;
import io.github.uchkun07.travelsystem.service.IAdminRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class AdminRoleServiceImpl implements IAdminRoleService {

    @Autowired
    private AdminRoleMapper adminRoleMapper;

    @Override
    @Transactional
    public AdminRole createRole(AdminRoleRequest request) {
        LambdaQueryWrapper<AdminRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminRole::getRoleName, request.getRoleName());
        if (adminRoleMapper.selectCount(wrapper) > 0) {
            throw new IllegalArgumentException("角色名称已存在: " + request.getRoleName());
        }

        AdminRole role = AdminRole.builder()
                .roleName(request.getRoleName())
                .roleDesc(request.getRoleDesc())
                .status(request.getStatus())
                .build();

        adminRoleMapper.insert(role);
        log.info("创建角色成功: {}", role.getRoleName());
        return role;
    }

    @Override
    @Transactional
    public void deleteRole(Integer roleId) {
        AdminRole role = adminRoleMapper.selectById(roleId);
        if (role == null) {
            throw new IllegalArgumentException("角色不存在");
        }
        adminRoleMapper.deleteById(roleId);
        log.info("删除角色成功: {}", role.getRoleName());
    }

    @Override
    @Transactional
    public void batchDeleteRoles(List<Integer> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            throw new IllegalArgumentException("角色ID列表不能为空");
        }
        @SuppressWarnings("deprecation")
        int count = adminRoleMapper.deleteBatchIds(roleIds);
        log.info("批量删除角色成功: 删除{}条", count);
    }

    @Override
    @Transactional
    public AdminRole updateRole(AdminRoleRequest request) {
        AdminRole role = adminRoleMapper.selectById(request.getRoleId());
        if (role == null) {
            throw new IllegalArgumentException("角色不存在");
        }

        if (request.getRoleName() != null && !request.getRoleName().equals(role.getRoleName())) {
            LambdaQueryWrapper<AdminRole> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AdminRole::getRoleName, request.getRoleName())
                   .ne(AdminRole::getRoleId, request.getRoleId());
            if (adminRoleMapper.selectCount(wrapper) > 0) {
                throw new IllegalArgumentException("角色名称已存在: " + request.getRoleName());
            }
            role.setRoleName(request.getRoleName());
        }

        if (request.getRoleDesc() != null) role.setRoleDesc(request.getRoleDesc());
        if (request.getStatus() != null) role.setStatus(request.getStatus());

        adminRoleMapper.updateById(role);
        log.info("更新角色成功: {}", role.getRoleName());
        return role;
    }

    @Override
    public PageResponse<AdminRole> queryRoles(RoleQueryRequest request) {
        LambdaQueryWrapper<AdminRole> wrapper = new LambdaQueryWrapper<>();
        
        if (request.getRoleName() != null && !request.getRoleName().trim().isEmpty()) {
            wrapper.like(AdminRole::getRoleName, request.getRoleName().trim());
        }
        if (request.getStatus() != null) {
            wrapper.eq(AdminRole::getStatus, request.getStatus());
        }

        wrapper.orderByDesc(AdminRole::getCreateTime);

        Page<AdminRole> page = new Page<>(request.getPageNum(), request.getPageSize());
        Page<AdminRole> result = adminRoleMapper.selectPage(page, wrapper);
        return PageResponse.of(result);
    }

    @Override
    public AdminRole getRoleById(Integer roleId) {
        AdminRole role = adminRoleMapper.selectById(roleId);
        if (role == null) {
            throw new IllegalArgumentException("角色不存在");
        }
        return role;
    }

    @Override
    public List<AdminRole> getAllRoles() {
        LambdaQueryWrapper<AdminRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminRole::getStatus, 1);
        return adminRoleMapper.selectList(wrapper);
    }
}
