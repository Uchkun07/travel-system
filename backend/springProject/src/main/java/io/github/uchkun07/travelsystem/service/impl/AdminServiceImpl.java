package io.github.uchkun07.travelsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.uchkun07.travelsystem.dto.LoginRequest;
import io.github.uchkun07.travelsystem.dto.LoginResponse;
import io.github.uchkun07.travelsystem.entity.Admin;
import io.github.uchkun07.travelsystem.mapper.AdminMapper;
import io.github.uchkun07.travelsystem.service.IAdminService;
import io.github.uchkun07.travelsystem.util.IpUtil;
import io.github.uchkun07.travelsystem.util.JwtUtil;
import io.github.uchkun07.travelsystem.util.PasswordUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 管理员服务实现类
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements IAdminService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private HttpServletRequest request;

    /**
     * 管理员登录
     *
     * @param loginRequest 登录请求
     * @return 登录响应信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginResponse login(LoginRequest loginRequest) {
        // 1. 参数校验
        if (loginRequest.getUsername() == null || loginRequest.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }

        // 2. 查询管理员
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Admin::getUsername, loginRequest.getUsername());
        Admin admin = this.getOne(queryWrapper);

        if (admin == null) {
            throw new IllegalArgumentException("用户名错误");
        }

        // 3. 检查账号状态
        if (admin.getStatus() == null || admin.getStatus() != 1) {
            throw new IllegalStateException("账号已被禁用");
        }

        // 4. 验证密码
        boolean passwordValid = PasswordUtil.verifyPassword(
                loginRequest.getPassword(),
                admin.getPasswordSalt(),
                admin.getPbkdf2Iterations(),
                admin.getPassword()
        );

        if (!passwordValid) {
            throw new IllegalArgumentException("密码错误");
        }

        // 5. 更新登录信息
        Admin updateAdmin = new Admin();
        updateAdmin.setAdminId(admin.getAdminId());
        updateAdmin.setLastLoginTime(LocalDateTime.now());
        updateAdmin.setLastLoginIp(IpUtil.getClientIp(request));
        updateAdmin.setLoginCount((admin.getLoginCount() == null ? 0 : admin.getLoginCount()) + 1);
        this.updateById(updateAdmin);

        // 6. 生成JWT token
        boolean rememberMe = loginRequest.getRememberMe() != null && loginRequest.getRememberMe();
        String token = jwtUtil.generateToken(admin.getAdminId(), admin.getUsername(), rememberMe);

        // 7. 构建登录响应
        return LoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(admin.getAdminId())
                .username(admin.getUsername())
                .fullName(admin.getFullName())
                .email(admin.getEmail())
                .phone(admin.getPhone())
                .build();
    }
}
