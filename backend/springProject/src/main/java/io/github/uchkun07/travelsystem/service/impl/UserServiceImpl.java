package io.github.uchkun07.travelsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.uchkun07.travelsystem.dto.UserLoginRequest;
import io.github.uchkun07.travelsystem.dto.UserRegisterRequest;
import io.github.uchkun07.travelsystem.dto.UserInfoResponse;
import io.github.uchkun07.travelsystem.dto.UserLoginResponse;
import io.github.uchkun07.travelsystem.entity.User;
import io.github.uchkun07.travelsystem.mapper.UserMapper;
import io.github.uchkun07.travelsystem.service.IUserService;
import io.github.uchkun07.travelsystem.service.IEmailService;
import io.github.uchkun07.travelsystem.util.JwtUtil;
import io.github.uchkun07.travelsystem.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 用户服务实现类(C端)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final IEmailService emailService;

    @Override
    @Transactional
    public UserLoginResponse register(UserRegisterRequest request) {
        // 1. 验证邮箱验证码
        if (!emailService.verifyCode(request.getEmail(), request.getCaptcha())) {
            return UserLoginResponse.builder()
                    .success(false)
                    .message("验证码错误或已过期")
                    .build();
        }

        // 2. 验证两次密码是否一致
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return UserLoginResponse.builder()
                    .success(false)
                    .message("两次密码输入不一致")
                    .build();
        }

        // 3. 检查用户名是否已存在
        LambdaQueryWrapper<User> usernameWrapper = new LambdaQueryWrapper<>();
        usernameWrapper.eq(User::getUsername, request.getUsername());
        if (userMapper.selectCount(usernameWrapper) > 0) {
            return UserLoginResponse.builder()
                    .success(false)
                    .message("用户名已存在")
                    .build();
        }

        // 4. 检查邮箱是否已存在
        LambdaQueryWrapper<User> emailWrapper = new LambdaQueryWrapper<>();
        emailWrapper.eq(User::getEmail, request.getEmail());
        if (userMapper.selectCount(emailWrapper) > 0) {
            return UserLoginResponse.builder()
                    .success(false)
                    .message("邮箱已被注册")
                    .build();
        }

        // 5. 创建新用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setNickname(request.getUsername()); // 默认昵称为用户名
        user.setStatus(1); // 默认启用

        // 使用 PBKDF2 加密密码
        try {
            String salt = PasswordUtil.generateSalt();
            user.setPasswordSalt(salt);
            
            String passwordHash = PasswordUtil.hashPassword(request.getPassword(), salt);
            user.setPassword(passwordHash);
        } catch (Exception e) {
            log.error("密码加密失败", e);
            return UserLoginResponse.builder()
                    .success(false)
                    .message("密码加密失败")
                    .build();
        }

        userMapper.insert(user);

        // 生成token
        String token = jwtUtil.generateToken(user.getUserId(), user.getUsername(), false);

        return UserLoginResponse.builder()
                .success(true)
                .message("注册成功")
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .avatar(user.getAvatarUrl())
                .fullName(user.getNickname())
                .token(token)
                .build();
    }

    @Override
    public UserLoginResponse login(UserLoginRequest request) {
        // 查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.getUsername());
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            return UserLoginResponse.builder()
                    .success(false)
                    .message("用户名或密码错误")
                    .build();
        }

        // 验证密码 (使用 PBKDF2)
        if (!PasswordUtil.verifyPassword(request.getPassword(), user.getPasswordSalt(), user.getPassword())) {
            return UserLoginResponse.builder()
                    .success(false)
                    .message("用户名或密码错误")
                    .build();
        }

        // 检查用户状态
        if (user.getStatus() != 1) {
            return UserLoginResponse.builder()
                    .success(false)
                    .message("账号已被禁用")
                    .build();
        }

        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(user);

        // 生成token (使用rememberMe参数)
        boolean rememberMe = request.getRememberMe() != null && request.getRememberMe();
        String token = jwtUtil.generateToken(user.getUserId(), user.getUsername(), rememberMe);

        return UserLoginResponse.builder()
                .success(true)
                .message("登录成功")
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .avatar(user.getAvatarUrl())
                .fullName(user.getNickname())
                .token(token)
                .build();
    }

    @Override
    public UserInfoResponse getUserInfo(String token) {
        // 解析token获取用户ID
        Long userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            return UserInfoResponse.builder()
                    .success(false)
                    .message("无效的token")
                    .build();
        }

        // 查询用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            return UserInfoResponse.builder()
                    .success(false)
                    .message("用户不存在")
                    .build();
        }

        return UserInfoResponse.builder()
                .success(true)
                .message("获取成功")
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getNickname())
                .avatar(user.getAvatarUrl())
                .status(user.getStatus())
                .build();
    }

    @Override
    public void logout(String token) {
        // 当前简单实现：登出成功不需要返回值
    }

    @Override
    public boolean checkUsernameAvailable(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return userMapper.selectCount(wrapper) == 0;
    }

    @Override
    public boolean checkEmailAvailable(String email) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, email);
        return userMapper.selectCount(wrapper) == 0;
    }
}
