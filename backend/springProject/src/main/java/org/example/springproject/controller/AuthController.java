package org.example.springproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.springproject.entity.dto.LoginRequest;
import org.example.springproject.entity.dto.RegisterRequest;
import org.example.springproject.service.IUserService;
import org.example.springproject.util.JwtUtil;
import org.example.springproject.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户认证控制器
 */
@Slf4j
@Tag(name = "用户认证", description = "用户登录注册相关接口")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private IUserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 用户注册
     */
    @Operation(summary = "用户注册", description = "新用户注册接口")
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody RegisterRequest request) {
        log.info("用户注册请求,用户名:{}, 邮箱:{}", request.getUsername(), request.getEmail());
        return userService.register(request);
    }

    /**
     * 用户登录
     */
    @Operation(summary = "用户登录", description = "用户登录接口,支持用户名或邮箱登录")
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest request) {
        log.info("用户登录请求,用户名:{}", request.getUsername());
        return userService.login(request);
    }

    /**
     * 用户登出
     */
    @Operation(summary = "用户登出", description = "用户退出登录")
    @PostMapping("/logout")
    public Map<String, Object> logout(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 从请求头获取token
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                
                // 获取令牌过期时间
                Long expirationTime = jwtUtil.getExpirationTime(token);
                if (expirationTime != null) {
                    // 计算剩余有效时间(秒)
                    long remainingTime = (expirationTime - System.currentTimeMillis()) / 1000;
                    
                    if (remainingTime > 0) {
                        // 将令牌加入黑名单,设置过期时间与令牌一致
                        String blacklistKey = "token_blacklist:" + token;
                        redisUtil.set(blacklistKey, "1", remainingTime);
                        log.info("用户登出成功,令牌已加入黑名单");
                    }
                }
            }
            
            result.put("success", true);
            result.put("message", "登出成功");
            
        } catch (Exception e) {
            result.put("success", true);
            result.put("message", "登出成功");
            log.warn("登出处理异常,但仍返回成功", e);
        }
        
        return result;
    }

    /**
     * 获取当前用户信息
     */
    @Operation(summary = "获取当前用户信息", description = "根据JWT令牌获取当前登录用户信息")
    @GetMapping("/userInfo")
    public Map<String, Object> getUserInfo(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 从请求属性中获取用户信息(由拦截器设置)
            Long userId = (Long) request.getAttribute("userId");
            
            if (userId == null) {
                result.put("success", false);
                result.put("message", "未找到用户信息");
                return result;
            }
            
            // 查询完整用户信息
            var user = userService.getById(userId);
            if (user == null) {
                result.put("success", false);
                result.put("message", "用户不存在");
                return result;
            }
            
            result.put("success", true);
            result.put("userId", user.getUserId());
            result.put("username", user.getUsername());
            result.put("email", user.getEmail());
            result.put("fullName", user.getFullName());
            result.put("avatar", user.getAvatar());
            result.put("status", user.getStatus());
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取用户信息失败: " + e.getMessage());
            log.error("获取用户信息异常", e);
        }
        
        return result;
    }

    /**
     * 检查用户名是否可用
     */
    @Operation(summary = "检查用户名", description = "检查用户名是否已被使用")
    @GetMapping("/checkUsername")
    public Map<String, Object> checkUsername(@RequestParam String username) {
        boolean exists = userService.getUserByUsername(username) != null;
        return Map.of(
                "success", true,
                "available", !exists,
                "message", exists ? "用户名已存在" : "用户名可用"
        );
    }

    /**
     * 检查邮箱是否可用
     */
    @Operation(summary = "检查邮箱", description = "检查邮箱是否已被注册")
    @GetMapping("/checkEmail")
    public Map<String, Object> checkEmail(@RequestParam String email) {
        boolean exists = userService.getUserByEmail(email) != null;
        return Map.of(
                "success", true,
                "available", !exists,
                "message", exists ? "邮箱已被注册" : "邮箱可用"
        );
    }
}
