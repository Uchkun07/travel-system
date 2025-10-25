package org.example.springproject.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.example.springproject.entity.User;
import org.example.springproject.entity.dto.LoginRequest;
import org.example.springproject.entity.dto.RegisterRequest;
import org.example.springproject.mapper.UserMapper;
import org.example.springproject.service.IEmailService;
import org.example.springproject.service.IUserService;
import org.example.springproject.util.JwtUtil;
import org.example.springproject.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private IEmailService emailService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Map<String, Object> register(RegisterRequest request) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 1. 参数验证
            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "用户名不能为空");
                return result;
            }

            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "密码不能为空");
                return result;
            }

            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "邮箱不能为空");
                return result;
            }

            if (request.getCaptcha() == null || request.getCaptcha().trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "验证码不能为空");
                return result;
            }

            // 2. 验证两次密码是否一致
            if (!request.getPassword().equals(request.getConfirmPassword())) {
                result.put("success", false);
                result.put("message", "两次输入的密码不一致");
                return result;
            }

            // 3. 验证邮箱验证码
            if (!emailService.verifyCode(request.getEmail(), request.getCaptcha())) {
                result.put("success", false);
                result.put("message", "验证码错误或已过期");
                return result;
            }

            // 4. 检查用户名是否已存在
            User existUser = getUserByUsername(request.getUsername());
            if (existUser != null) {
                result.put("success", false);
                result.put("message", "用户名已存在");
                return result;
            }

            // 5. 检查邮箱是否已被注册
            User existEmail = getUserByEmail(request.getEmail());
            if (existEmail != null) {
                result.put("success", false);
                result.put("message", "该邮箱已被注册");
                return result;
            }

            // 6. 创建新用户
            User user = new User();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());

            // 生成盐值和密码哈希
            try {
                String salt = PasswordUtil.generateSalt();
                user.setSalt(salt);

                int iterations = PasswordUtil.getDefaultIterations();
                user.setIterations(iterations);

                String passwordHash = PasswordUtil.hashPassword(request.getPassword(), salt, iterations);
                user.setPasswordHash(passwordHash);
            } catch (Exception e) {
                result.put("success", false);
                result.put("message", "密码加密失败");
                log.error("密码加密失败", e);
                return result;
            }

            // 设置其他默认值
            user.setStatus((byte) 1); // 1表示正常状态
            user.setRegisterSource("web");
            user.setRegisterTime(new Date());
            // 设置默认头像
            user.setAvatar("/img/defaultavatar.png");

            // 保存用户
            boolean saved = save(user);

            if (saved) {
                result.put("success", true);
                result.put("message", "注册成功");
                result.put("userId", user.getUserId());
                result.put("username", user.getUsername());
                log.info("用户注册成功,用户名:{}, 邮箱:{}", user.getUsername(), user.getEmail());
            } else {
                result.put("success", false);
                result.put("message", "注册失败,请稍后重试");
            }

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "注册失败: " + e.getMessage());
            log.error("用户注册异常", e);
        }

        return result;
    }

    @Override
    public Map<String, Object> login(LoginRequest request) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 1. 参数验证
            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "用户名不能为空");
                return result;
            }

            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "密码不能为空");
                return result;
            }

            // 2. 查找用户(智能判断是用户名还是邮箱)
            User user = null;
            String loginInput = request.getUsername().trim();
            
            // 判断输入是否为邮箱格式
            if (loginInput.contains("@")) {
                // 输入包含@符号,优先当作邮箱查询
                log.debug("检测到邮箱格式,优先按邮箱查询: {}", loginInput);
                user = getUserByEmail(loginInput);
                if (user == null) {
                    // 如果邮箱查不到,再尝试用户名(防止用户名中包含@)
                    log.debug("邮箱未找到,尝试按用户名查询: {}", loginInput);
                    user = getUserByUsername(loginInput);
                }
            } else {
                // 输入不包含@符号,优先当作用户名查询
                log.debug("检测到用户名格式,优先按用户名查询: {}", loginInput);
                user = getUserByUsername(loginInput);
                if (user == null) {
                    // 如果用户名查不到,再尝试邮箱
                    log.debug("用户名未找到,尝试按邮箱查询: {}", loginInput);
                    user = getUserByEmail(loginInput);
                }
            }

            if (user == null) {
                result.put("success", false);
                result.put("message", "用户名或密码错误");
                log.warn("登录失败,用户不存在: {}", loginInput);
                return result;
            }
            
            log.debug("找到用户,userId: {}, username: {}, email: {}", user.getUserId(), user.getUsername(), user.getEmail());

            // 3. 检查用户状态
            if (user.getStatus() == null || user.getStatus() != 1) {
                result.put("success", false);
                result.put("message", "账号已被禁用,请联系管理员");
                return result;
            }

            // 4. 验证密码
            boolean passwordMatch = PasswordUtil.verifyPassword(
                    request.getPassword(),
                    user.getSalt(),
                    user.getIterations(),
                    user.getPasswordHash()
            );

            if (!passwordMatch) {
                result.put("success", false);
                result.put("message", "用户名或密码错误");
                log.warn("登录失败,密码错误,用户名:{}", request.getUsername());
                return result;
            }

            // 5. 更新最后登录时间
            user.setLastLoginTime(new Date());
            updateById(user);

            // 6. 生成JWT令牌
            boolean rememberMe = request.getRememberMe() != null && request.getRememberMe();
            String token = jwtUtil.generateToken(user.getUserId().longValue(), user.getUsername(), rememberMe);

            // 7. 返回登录成功信息
            result.put("success", true);
            result.put("message", "登录成功");
            result.put("userId", user.getUserId());
            result.put("username", user.getUsername());
            result.put("email", user.getEmail());
            result.put("avatar", user.getAvatar());
            result.put("fullName", user.getFullName());
            result.put("token", token);

            log.info("用户登录成功,用户名:{}", user.getUsername());

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "登录失败: " + e.getMessage());
            log.error("用户登录异常", e);
        }

        return result;
    }

    @Override
    public User getUserByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        return getOne(wrapper);
    }

    @Override
    public User getUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("email", email);
        return getOne(wrapper);
    }
}
