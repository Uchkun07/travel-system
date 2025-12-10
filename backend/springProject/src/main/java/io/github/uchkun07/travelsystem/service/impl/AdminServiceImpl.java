package io.github.uchkun07.travelsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.uchkun07.travelsystem.dto.*;
import io.github.uchkun07.travelsystem.entity.Admin;
import io.github.uchkun07.travelsystem.mapper.AdminMapper;
import io.github.uchkun07.travelsystem.mapper.AdminRolePermissionMapper;
import io.github.uchkun07.travelsystem.mapper.AdminRoleRelationMapper;
import io.github.uchkun07.travelsystem.service.IAdminService;
import io.github.uchkun07.travelsystem.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * Token黑名单的Redis key前缀
     */
    private static final String TOKEN_BLACKLIST_PREFIX = "token:blacklist:";

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
            throw new RuntimeException("密码错误");
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

    /**
     * 管理员登出
     * 将token加入Redis黑名单,有效期设置为token的剩余有效时间
     * @param token JWT令牌
     */
    @Override
    public void logout(String token) {
        try {
            // 验证token是否有效
            if (!jwtUtil.validateToken(token)) {
                throw new IllegalArgumentException("无效的token");
            }

            // 获取token的过期时间
            Long expirationTime = jwtUtil.getExpirationTime(token);
            if (expirationTime == null) {
                throw new IllegalArgumentException("无法获取token过期时间");
            }

            // 计算token的剩余有效时间(秒)
            long currentTime = System.currentTimeMillis();
            long remainingTime = (expirationTime - currentTime) / 1000;

            if (remainingTime > 0) {
                // 将token加入黑名单,设置过期时间为token的剩余有效时间
                String blacklistKey = TOKEN_BLACKLIST_PREFIX + token;
                redisTemplate.opsForValue().set(blacklistKey, "1", remainingTime, TimeUnit.SECONDS);
                log.info("Token已加入黑名单,剩余有效时间: {} 秒", remainingTime);
            } else {
                log.warn("Token已过期,无需加入黑名名单");
            }
        } catch (Exception e) {
            log.error("登出失败: {}", e.getMessage(), e);
            throw new RuntimeException("登出失败: " + e.getMessage());
        }
    }

    /**
     * 检查token是否在黑名单中
     * @param token JWT令牌
     * @return true表示在黑名单中
     */
    public boolean isTokenBlacklisted(String token) {
        String blacklistKey = TOKEN_BLACKLIST_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(blacklistKey));
    }

    /**
     * 创建管理员
     * @param request 创建请求
     * @return 创建的管理员
     */
    @Override
    @Transactional
    public Admin createAdmin(AdminCreateRequest request) {
        // 1. 检查用户名是否已存在
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Admin::getUsername, request.getUsername());
        if (adminMapper.selectCount(queryWrapper) > 0) {
            throw new IllegalArgumentException("用户名已存在");
        }

        // 2. 生成密码盐值和加密密码
        String salt = generateSalt();
        int iterations = 65536;
        String encryptedPassword = encryptPassword(request.getPassword(), salt, iterations);

        // 3. 构建管理员实体
        Admin admin = Admin.builder()
                .username(request.getUsername())
                .password(encryptedPassword)
                .passwordSalt(salt)
                .pbkdf2Iterations(iterations)
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .status(request.getStatus() != null ? request.getStatus() : 1)
                .loginCount(0)
                .build();

        // 4. 保存到数据库
        adminMapper.insert(admin);
        log.info("创建管理员成功: adminId={}, username={}", admin.getAdminId(), admin.getUsername());

        return admin;
    }

    /**
     * 删除管理员
     * @param adminId 管理员ID
     */
    @Override
    @Transactional
    public void deleteAdmin(Long adminId) {
        Admin admin = adminMapper.selectById(adminId);
        if (admin == null) {
            throw new IllegalArgumentException("管理员不存在");
        }

        // 删除管理员
        adminMapper.deleteById(adminId);
        log.info("删除管理员成功: adminId={}, username={}", adminId, admin.getUsername());
    }

    /**
     * 批量删除管理员
     * @param adminIds 管理员ID列表
     */
    @Override
    @Transactional
    public void batchDeleteAdmins(List<Long> adminIds) {
        if (adminIds == null || adminIds.isEmpty()) {
            throw new IllegalArgumentException("管理员ID列表不能为空");
        }

        // 批量删除
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Admin::getAdminId, adminIds);
        adminMapper.delete(wrapper);
        
        log.info("批量删除管理员成功: count={}", adminIds.size());
    }

    /**
     * 更新管理员信息
     * @param request 更新请求
     * @return 更新后的管理员
     */
    @Override
    @Transactional
    public Admin updateAdmin(AdminUpdateRequest request) {
        // 1. 检查管理员是否存在
        Admin admin = adminMapper.selectById(request.getAdminId());
        if (admin == null) {
            throw new IllegalArgumentException("管理员不存在");
        }

        // 2. 更新字段
        if (StringUtils.hasText(request.getFullName())) {
            admin.setFullName(request.getFullName());
        }
        if (StringUtils.hasText(request.getPhone())) {
            admin.setPhone(request.getPhone());
        }
        if (StringUtils.hasText(request.getEmail())) {
            admin.setEmail(request.getEmail());
        }
        if (request.getStatus() != null) {
            admin.setStatus(request.getStatus());
        }

        // 3. 保存更新
        adminMapper.updateById(admin);
        log.info("更新管理员成功: adminId={}", admin.getAdminId());

        return admin;
    }

    /**
     * 修改管理员密码
     * @param request 密码修改请求
     */
    @Override
    @Transactional
    public void updatePassword(AdminPasswordUpdateRequest request) {
        // 1. 检查管理员是否存在
        Admin admin = adminMapper.selectById(request.getAdminId());
        if (admin == null) {
            throw new IllegalArgumentException("管理员不存在");
        }

        // 2. 验证旧密码
        String encryptedOldPassword = encryptPassword(
            request.getOldPassword(), 
            admin.getPasswordSalt(), 
            admin.getPbkdf2Iterations()
        );
        if (!encryptedOldPassword.equals(admin.getPassword())) {
            throw new IllegalArgumentException("旧密码错误");
        }

        // 3. 生成新的盐值和加密密码
        String salt = generateSalt();
        int iterations = 65536;
        String encryptedPassword = encryptPassword(request.getNewPassword(), salt, iterations);

        // 4. 更新密码
        admin.setPassword(encryptedPassword);
        admin.setPasswordSalt(salt);
        admin.setPbkdf2Iterations(iterations);
        adminMapper.updateById(admin);

        log.info("修改管理员密码成功: adminId={}", admin.getAdminId());
    }

    /**
     * 分页查询管理员
     * @param request 查询请求
     * @return 分页结果
     */
    @Override
    public Page<Admin> queryAdmins(AdminQueryRequest request) {
        // 构建分页对象
        Page<Admin> page = new Page<>(request.getCurrent(), request.getPageSize());

        // 构建查询条件
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(request.getUsername()), Admin::getUsername, request.getUsername())
                .like(StringUtils.hasText(request.getFullName()), Admin::getFullName, request.getFullName())
                .eq(StringUtils.hasText(request.getPhone()), Admin::getPhone, request.getPhone())
                .eq(StringUtils.hasText(request.getEmail()), Admin::getEmail, request.getEmail())
                .eq(request.getStatus() != null, Admin::getStatus, request.getStatus())
                .orderByDesc(Admin::getCreateTime);

        // 执行分页查询
        return adminMapper.selectPage(page, wrapper);
    }

    /**
     * 根据ID查询管理员详情
     * @param adminId 管理员ID
     * @return 管理员详情
     */
    @Override
    public Admin getAdminById(Long adminId) {
        Admin admin = adminMapper.selectById(adminId);
        if (admin == null) {
            throw new IllegalArgumentException("管理员不存在");
        }
        // 清空密码等敏感信息
        admin.setPassword(null);
        admin.setPasswordSalt(null);
        admin.setPbkdf2Iterations(null);
        return admin;
    }

    /**
     * 生成随机盐值
     * @return 盐值
     */
    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        
        StringBuilder hexString = new StringBuilder();
        for (byte b : salt) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * 加密密码（PBKDF2算法）
     * @param password 原始密码
     * @param salt 盐值
     * @param iterations 迭代次数
     * @return 加密后的密码
     */
    private String encryptPassword(String password, String salt, int iterations) {
        try {
            KeySpec spec = new PBEKeySpec(
                password.toCharArray(), 
                salt.getBytes(), 
                iterations, 
                256
            );
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = factory.generateSecret(spec).getEncoded();
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            log.error("密码加密失败", e);
            throw new RuntimeException("密码加密失败");
        }
    }
}

