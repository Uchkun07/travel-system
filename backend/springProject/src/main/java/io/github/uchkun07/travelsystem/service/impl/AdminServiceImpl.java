package io.github.uchkun07.travelsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.uchkun07.travelsystem.dto.AdminLoginRequest;
import io.github.uchkun07.travelsystem.dto.AdminLoginResponse;
import io.github.uchkun07.travelsystem.entity.Admin;
import io.github.uchkun07.travelsystem.mapper.AdminMapper;
import io.github.uchkun07.travelsystem.mapper.AdminRolePermissionMapper;
import io.github.uchkun07.travelsystem.mapper.AdminRoleRelationMapper;
import io.github.uchkun07.travelsystem.service.IAdminService;
import io.github.uchkun07.travelsystem.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.spec.KeySpec;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 管理员服务实现类
 */
@Slf4j
@Service
public class AdminServiceImpl implements IAdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private AdminRoleRelationMapper adminRoleRelationMapper;

    @Autowired
    private AdminRolePermissionMapper adminRolePermissionMapper;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 管理员登录
     * @param request 登录请求
     * @return 登录响应（包含token）
     */
    @Override
    @Transactional
    public AdminLoginResponse login(AdminLoginRequest request) {
        // 1. 查询管理员信息
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Admin::getUsername, request.getUsername());
        Admin admin = adminMapper.selectOne(queryWrapper);

        if (admin == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 2. 验证账号状态
        if (admin.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用,请联系系统管理员");
        }

        // 3. 验证密码
        if (!verifyPassword(request.getPassword(), admin.getPassword(), 
                           admin.getPasswordSalt(), admin.getPbkdf2Iterations())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 4. 查询管理员的角色
        List<Integer> roleIds = adminRoleRelationMapper.selectRoleIdsByAdminId(admin.getAdminId());
        
        // 5. 查询角色对应的权限编码
        List<String> permissions = new ArrayList<>();
        if (roleIds != null && !roleIds.isEmpty()) {
            permissions = adminRolePermissionMapper.selectPermissionCodesByRoleIds(roleIds);
        }

        // 6. 生成JWT令牌（包含管理员信息和权限）
        String token = jwtUtil.generateAdminToken(
            admin.getAdminId(),
            admin.getUsername(),
            admin.getFullName(),
            admin.getPhone(),
            admin.getEmail(),
            permissions,
            request.getRememberMe() != null && request.getRememberMe()
        );

        // 7. 更新登录信息
        admin.setLastLoginTime(LocalDateTime.now());
        admin.setLoginCount(admin.getLoginCount() == null ? 1 : admin.getLoginCount() + 1);
        adminMapper.updateById(admin);

        // 8. 构建响应
        return AdminLoginResponse.builder()
                .token(token)
                .adminId(admin.getAdminId())
                .username(admin.getUsername())
                .fullName(admin.getFullName())
                .phone(admin.getPhone())
                .email(admin.getEmail())
                .permissions(permissions)
                .build();
    }

    /**
     * 验证密码（PBKDF2算法）
     * @param inputPassword 输入的密码
     * @param storedPassword 存储的加密密码
     * @param salt 盐值
     * @param iterations 迭代次数
     * @return 是否匹配
     */
    private boolean verifyPassword(String inputPassword, String storedPassword, 
                                   String salt, Integer iterations) {
        try {
            // 使用PBKDF2算法加密输入密码
            KeySpec spec = new PBEKeySpec(
                inputPassword.toCharArray(), 
                salt.getBytes(), 
                iterations, 
                256
            );
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = factory.generateSecret(spec).getEncoded();
            
            // 转换为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            // 比较加密后的密码
            return hexString.toString().equals(storedPassword);
        } catch (Exception e) {
            log.error("密码验证失败", e);
            return false;
        }
    }

    /**
     * 根据管理员ID获取权限列表
     * @param adminId 管理员ID
     * @return 权限编码列表
     */
    @Override
    public List<String> getAdminPermissions(Long adminId) {
        // 查询管理员的角色
        List<Integer> roleIds = adminRoleRelationMapper.selectRoleIdsByAdminId(adminId);
        
        // 查询角色对应的权限编码
        if (roleIds != null && !roleIds.isEmpty()) {
            return adminRolePermissionMapper.selectPermissionCodesByRoleIds(roleIds);
        }
        
        return new ArrayList<>();
    }
}
