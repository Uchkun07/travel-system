package io.github.uchkun07.travelsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.uchkun07.travelsystem.entity.AdminRole;
import io.github.uchkun07.travelsystem.entity.AdminRoleRelation;
import io.github.uchkun07.travelsystem.mapper.AdminRoleMapper;
import io.github.uchkun07.travelsystem.mapper.AdminRoleRelationMapper;
import io.github.uchkun07.travelsystem.service.IAdminRoleRelationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AdminRoleRelationServiceImpl implements IAdminRoleRelationService {

    @Autowired
    private AdminRoleRelationMapper roleRelationMapper;

    @Autowired
    private AdminRoleMapper roleMapper;

    @Override
    @Transactional
    public void bindRolesToAdmin(Long adminId, List<Integer> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            throw new IllegalArgumentException("角色ID列表不能为空");
        }

        for (Integer roleId : roleIds) {
            LambdaQueryWrapper<AdminRoleRelation> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AdminRoleRelation::getAdminId, adminId)
                   .eq(AdminRoleRelation::getRoleId, roleId);
            
            if (roleRelationMapper.selectCount(wrapper) == 0) {
                AdminRoleRelation relation = AdminRoleRelation.builder()
                        .adminId(adminId)
                        .roleId(roleId)
                        .build();
                roleRelationMapper.insert(relation);
            }
        }
        log.info("管理员绑定角色成功: adminId={}, roleIds={}", adminId, roleIds);
    }

    @Override
    @Transactional
    public void unbindRolesFromAdmin(Long adminId, List<Integer> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            throw new IllegalArgumentException("角色ID列表不能为空");
        }

        for (Integer roleId : roleIds) {
            LambdaQueryWrapper<AdminRoleRelation> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AdminRoleRelation::getAdminId, adminId)
                   .eq(AdminRoleRelation::getRoleId, roleId);
            roleRelationMapper.delete(wrapper);
        }
        log.info("管理员解绑角色成功: adminId={}, roleIds={}", adminId, roleIds);
    }

    @Override
    @Transactional
    public void unbindAllRolesFromAdmin(Long adminId) {
        LambdaQueryWrapper<AdminRoleRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminRoleRelation::getAdminId, adminId);
        roleRelationMapper.delete(wrapper);
        log.info("管理员解绑所有角色成功: adminId={}", adminId);
    }

    @Override
    public List<AdminRole> getAdminRoles(Long adminId) {
        LambdaQueryWrapper<AdminRoleRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminRoleRelation::getAdminId, adminId);
        List<AdminRoleRelation> relations = roleRelationMapper.selectList(wrapper);

        if (relations.isEmpty()) {
            return new ArrayList<>();
        }

        List<Integer> roleIds = relations.stream()
                .map(AdminRoleRelation::getRoleId)
                .collect(Collectors.toList());

        LambdaQueryWrapper<AdminRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(AdminRole::getRoleId, roleIds);
        return roleMapper.selectList(queryWrapper);
    }
}
