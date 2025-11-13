package io.github.uchkun07.travelsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.uchkun07.travelsystem.dto.AdminPermissionRequest;
import io.github.uchkun07.travelsystem.dto.PageResponse;
import io.github.uchkun07.travelsystem.dto.PermissionQueryRequest;
import io.github.uchkun07.travelsystem.entity.AdminPermission;
import io.github.uchkun07.travelsystem.mapper.AdminPermissionMapper;
import io.github.uchkun07.travelsystem.service.IAdminPermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class AdminPermissionServiceImpl implements IAdminPermissionService {

    @Autowired
    private AdminPermissionMapper adminPermissionMapper;

    @Override
    @Transactional
    public AdminPermission createPermission(AdminPermissionRequest request) {
        LambdaQueryWrapper<AdminPermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminPermission::getPermissionCode, request.getPermissionCode());
        if (adminPermissionMapper.selectCount(wrapper) > 0) {
            throw new IllegalArgumentException("权限编码已存在: " + request.getPermissionCode());
        }

        AdminPermission permission = AdminPermission.builder()
                .permissionCode(request.getPermissionCode())
                .permissionName(request.getPermissionName())
                .resourceType(request.getResourceType())
                .resourcePath(request.getResourcePath())
                .isSensitive(request.getIsSensitive())
                .sortOrder(request.getSortOrder())
                .build();

        adminPermissionMapper.insert(permission);
        log.info("创建权限成功: {}", permission.getPermissionCode());
        return permission;
    }

    @Override
    @Transactional
    public void deletePermission(Integer permissionId) {
        AdminPermission permission = adminPermissionMapper.selectById(permissionId);
        if (permission == null) {
            throw new IllegalArgumentException("权限不存在");
        }
        adminPermissionMapper.deleteById(permissionId);
        log.info("删除权限成功: {}", permission.getPermissionCode());
    }

    @Override
    @Transactional
    public void batchDeletePermissions(List<Integer> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            throw new IllegalArgumentException("权限ID列表不能为空");
        }
        @SuppressWarnings("deprecation")
        int count = adminPermissionMapper.deleteBatchIds(permissionIds);
        log.info("批量删除权限成功: 删除{}条", count);
    }

    @Override
    @Transactional
    public AdminPermission updatePermission(AdminPermissionRequest request) {
        AdminPermission permission = adminPermissionMapper.selectById(request.getPermissionId());
        if (permission == null) {
            throw new IllegalArgumentException("权限不存在");
        }

        if (request.getPermissionCode() != null && !request.getPermissionCode().equals(permission.getPermissionCode())) {
            LambdaQueryWrapper<AdminPermission> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AdminPermission::getPermissionCode, request.getPermissionCode())
                   .ne(AdminPermission::getPermissionId, request.getPermissionId());
            if (adminPermissionMapper.selectCount(wrapper) > 0) {
                throw new IllegalArgumentException("权限编码已存在: " + request.getPermissionCode());
            }
            permission.setPermissionCode(request.getPermissionCode());
        }

        if (request.getPermissionName() != null) permission.setPermissionName(request.getPermissionName());
        if (request.getResourceType() != null) permission.setResourceType(request.getResourceType());
        if (request.getResourcePath() != null) permission.setResourcePath(request.getResourcePath());
        if (request.getIsSensitive() != null) permission.setIsSensitive(request.getIsSensitive());
        if (request.getSortOrder() != null) permission.setSortOrder(request.getSortOrder());

        adminPermissionMapper.updateById(permission);
        log.info("更新权限成功: {}", permission.getPermissionCode());
        return permission;
    }

    @Override
    public PageResponse<AdminPermission> queryPermissions(PermissionQueryRequest request) {
        LambdaQueryWrapper<AdminPermission> wrapper = new LambdaQueryWrapper<>();
        
        if (request.getPermissionCode() != null && !request.getPermissionCode().trim().isEmpty()) {
            wrapper.like(AdminPermission::getPermissionCode, request.getPermissionCode().trim());
        }
        if (request.getPermissionName() != null && !request.getPermissionName().trim().isEmpty()) {
            wrapper.like(AdminPermission::getPermissionName, request.getPermissionName().trim());
        }
        if (request.getResourceType() != null) {
            wrapper.eq(AdminPermission::getResourceType, request.getResourceType());
        }
        if (request.getIsSensitive() != null) {
            wrapper.eq(AdminPermission::getIsSensitive, request.getIsSensitive());
        }

        wrapper.orderByAsc(AdminPermission::getSortOrder)
               .orderByDesc(AdminPermission::getCreateTime);

        Page<AdminPermission> page = new Page<>(request.getPageNum(), request.getPageSize());
        Page<AdminPermission> result = adminPermissionMapper.selectPage(page, wrapper);
        return PageResponse.of(result);
    }

    @Override
    public AdminPermission getPermissionById(Integer permissionId) {
        AdminPermission permission = adminPermissionMapper.selectById(permissionId);
        if (permission == null) {
            throw new IllegalArgumentException("权限不存在");
        }
        return permission;
    }

    @Override
    public List<AdminPermission> getAllPermissions() {
        LambdaQueryWrapper<AdminPermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(AdminPermission::getSortOrder);
        return adminPermissionMapper.selectList(wrapper);
    }
}
