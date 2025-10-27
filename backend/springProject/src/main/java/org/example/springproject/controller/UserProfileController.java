package org.example.springproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.springproject.entity.User;
import org.example.springproject.entity.dto.UpdateProfileRequest;
import org.example.springproject.entity.dto.UpdateProfileResponse;
import org.example.springproject.service.IUserService;
import org.example.springproject.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;

/**
 * 用户资料管理控制器
 */
@Tag(name = "用户资料管理", description = "用户个人资料相关操作（需要JWT认证）")
@RestController
@RequestMapping("/v1/users")
public class UserProfileController {

    @Autowired
    private IUserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 更新用户资料
     * @param authorization JWT令牌（格式：Bearer {token}）
     * @param request 更新资料请求
     * @return 更新结果和新的token
     */
    @Operation(summary = "更新用户资料", description = "更新当前登录用户的基本资料信息，需要JWT认证")
    @PutMapping("/profile")
    public ResponseEntity<UpdateProfileResponse> updateProfile(
            @Parameter(description = "JWT令牌", required = true)
            @RequestHeader("Authorization") String authorization,
            @Parameter(description = "更新资料请求", required = true)
            @Valid @RequestBody UpdateProfileRequest request) {
        
        try {
            // 1. 验证并解析 JWT 令牌
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(UpdateProfileResponse.error("无效的令牌格式"));
            }

            String token = authorization.substring(7);
            
            // 验证令牌有效性
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(UpdateProfileResponse.error("令牌无效或已过期"));
            }

            // 2. 从令牌中获取用户ID
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(UpdateProfileResponse.error("无法获取用户信息"));
            }

            // 3. 获取用户信息
            User user = userService.getById(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(UpdateProfileResponse.error("用户不存在"));
            }

            // 4. 更新允许修改的字段
            boolean hasChanges = false;
            
            if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
                // 检查用户名是否已被使用
                User existingUser = userService.getUserByUsername(request.getUsername());
                if (existingUser != null && !existingUser.getUserId().equals(user.getUserId())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(UpdateProfileResponse.error("用户名已被使用"));
                }
                user.setUsername(request.getUsername());
                hasChanges = true;
            }
            
            if (request.getFullName() != null && !request.getFullName().equals(user.getFullName())) {
                user.setFullName(request.getFullName());
                hasChanges = true;
            }
            
            if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
                // 检查邮箱是否已被使用
                User existingUser = userService.getUserByEmail(request.getEmail());
                if (existingUser != null && !existingUser.getUserId().equals(user.getUserId())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(UpdateProfileResponse.error("邮箱已被使用"));
                }
                user.setEmail(request.getEmail());
                hasChanges = true;
            }
            
            if (request.getPhone() != null && !request.getPhone().equals(user.getPhone())) {
                user.setPhone(request.getPhone());
                hasChanges = true;
            }
            
            if (request.getGender() != null && !request.getGender().equals(user.getGender())) {
                user.setGender(request.getGender());
                hasChanges = true;
            }
            
            if (request.getBirthday() != null && !request.getBirthday().equals(user.getBirthday())) {
                user.setBirthday(request.getBirthday());
                hasChanges = true;
            }
            
            if (request.getAvatar() != null && !request.getAvatar().equals(user.getAvatar())) {
                user.setAvatar(request.getAvatar());
                hasChanges = true;
            }

            // 5. 如果没有任何改变，直接返回
            if (!hasChanges) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(UpdateProfileResponse.error("没有需要更新的内容"));
            }

            // 6. 执行更新
            boolean success = userService.updateById(user);
            if (!success) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(UpdateProfileResponse.error("资料更新失败"));
            }

            // 7. 生成新的JWT令牌（包含完整用户信息）
            String newToken = jwtUtil.generateTokenWithUserInfo(
                    user.getUserId().longValue(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getFullName(),
                    user.getAvatar(),
                    user.getPhone(),
                    user.getGender(),
                    user.getBirthday(),
                    true // 默认记住用户
            );

            // 8. 构建用户资料数据
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            UpdateProfileResponse.UserProfileData userProfileData = 
                    new UpdateProfileResponse.UserProfileData(
                        user.getUserId(),
                        user.getUsername(),
                        user.getFullName(),
                        user.getEmail(),
                        user.getPhone(),
                        user.getGender(),
                        user.getBirthday() != null ? sdf.format(user.getBirthday()) : null,
                        user.getAvatar()
                    );

            // 9. 返回成功响应
            return ResponseEntity.ok(UpdateProfileResponse.success(newToken, userProfileData));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(UpdateProfileResponse.error("系统错误: " + e.getMessage()));
        }
    }
}
