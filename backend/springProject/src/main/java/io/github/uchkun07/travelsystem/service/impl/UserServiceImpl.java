package io.github.uchkun07.travelsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.uchkun07.travelsystem.dto.*;
import io.github.uchkun07.travelsystem.entity.User;
import io.github.uchkun07.travelsystem.entity.UserProfile;
import io.github.uchkun07.travelsystem.mapper.UserMapper;
import io.github.uchkun07.travelsystem.mapper.UserProfileMapper;
import io.github.uchkun07.travelsystem.service.IUserService;
import io.github.uchkun07.travelsystem.service.IEmailService;
import io.github.uchkun07.travelsystem.service.IUserPreferenceService;
import io.github.uchkun07.travelsystem.util.JwtUtil;
import io.github.uchkun07.travelsystem.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 用户服务实现类(C端)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserMapper userMapper;
    private final UserProfileMapper userProfileMapper;
    private final JwtUtil jwtUtil;
    private final IEmailService emailService;
    private final IUserPreferenceService userPreferenceService;

    @Value("${file.upload.avatar-dir:src/main/resources/static/avatars}")
    private String avatarUploadDir;

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
        user.setAvatarUrl("/avatars/defaultavatar.png");
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

        // 初始化用户偏好
        userPreferenceService.initializeUserPreference(user.getUserId());

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
        // 判断输入的是邮箱还是用户名（邮箱包含@符号）
        String loginInput = request.getUsername();
        boolean isEmail = loginInput.contains("@");
        
        // 根据输入类型查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (isEmail) {
            wrapper.eq(User::getEmail, loginInput);
        } else {
            wrapper.eq(User::getUsername, loginInput);
        }
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

    @Override
    public UserProfileResponse getUserProfile(String token) {
        // 从token中提取userId
        token = token.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(token);

        // 查询用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 查询用户基本信息
        LambdaQueryWrapper<UserProfile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserProfile::getUserId, userId);
        UserProfile profile = userProfileMapper.selectOne(wrapper);

        // 构建响应
        UserProfileResponse.UserProfileResponseBuilder builder = UserProfileResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .avatar(user.getAvatarUrl());

        if (profile != null) {
            builder.fullName(profile.getFullName())
                    .phone(profile.getPhone())
                    .gender(profile.getGender())
                    .birthday(profile.getBirthday() != null ? profile.getBirthday().toString() : null)
                    .residentAddress(profile.getResidentAddress());
        }

        return builder.build();
    }

    @Override
    @Transactional
    public UserProfileResponse updateUserProfile(String token, UpdateUserProfileRequest request) {
        // 从token中提取userId
        token = token.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(token);

        // 查询用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 查询或创建用户基本信息
        LambdaQueryWrapper<UserProfile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserProfile::getUserId, userId);
        UserProfile profile = userProfileMapper.selectOne(wrapper);

        if (profile == null) {
            // 创建新的基本信息记录
            profile = UserProfile.builder()
                    .userId(userId)
                    .fullName(request.getFullName())
                    .phone(request.getPhone())
                    .gender(request.getGender())
                    .birthday(request.getBirthday() != null ? LocalDate.parse(request.getBirthday()) : null)
                    .residentAddress(request.getResidentAddress())
                    .build();
            userProfileMapper.insert(profile);
        } else {
            // 更新现有信息
            if (request.getFullName() != null) {
                profile.setFullName(request.getFullName());
            }
            if (request.getPhone() != null) {
                profile.setPhone(request.getPhone());
            }
            if (request.getGender() != null) {
                profile.setGender(request.getGender());
            }
            if (request.getBirthday() != null) {
                profile.setBirthday(LocalDate.parse(request.getBirthday()));
            }
            if (request.getResidentAddress() != null) {
                profile.setResidentAddress(request.getResidentAddress());
            }
            userProfileMapper.updateById(profile);
        }

        // 返回更新后的用户信息
        return getUserProfile("Bearer " + token);
    }

    @Override
    @Transactional
    public AvatarUploadResponse uploadAvatar(String token, MultipartFile file) throws IOException {
        // 从token中提取userId
        token = token.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(token);

        // 查询用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 验证文件
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("只能上传图片文件");
        }

        // 验证文件大小（限制5MB）
        long maxSize = 5 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("文件大小不能超过5MB");
        }

        // 生成文件名
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".") 
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".jpg";
        String filename = UUID.randomUUID().toString() + extension;

        // 确保上传目录存在
        Path uploadPath = Paths.get(avatarUploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 保存文件
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath);

        // 构建URL（相对路径）
        String avatarUrl = "/avatars/" + filename;

        // 更新用户头像URL
        user.setAvatarUrl(avatarUrl);
        userMapper.updateById(user);

        // 生成新的token（包含更新后的头像信息）
        String newToken = jwtUtil.generateToken(user.getUserId(), user.getUsername(), false);

        log.info("用户 {} 上传头像成功: {}", userId, avatarUrl);

        return AvatarUploadResponse.builder()
                .avatarUrl(avatarUrl)
                .token(newToken)
                .build();
    }
}
