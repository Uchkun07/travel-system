package org.example.springproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.springproject.entity.User;
import org.example.springproject.entity.dto.*;
import org.example.springproject.service.IUserService;
import org.example.springproject.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * 用户资料管理控制器
 */
@Tag(name = "用户资料管理", description = "用户个人资料相关操作（需要JWT认证）")
@RestController
@RequestMapping("/v1/users")
public class UserProfileController {

    private static final Logger logger = LoggerFactory.getLogger(UserProfileController.class);

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
        
        logger.info("接收到更新用户资料请求");
        
        try {
            // 1. 验证并解析 JWT 令牌
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                logger.warn("无效的令牌格式");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(UpdateProfileResponse.error("无效的令牌格式"));
            }

            String token = authorization.substring(7);
            
            // 验证令牌有效性
            if (!jwtUtil.validateToken(token)) {
                logger.warn("令牌无效或已过期");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(UpdateProfileResponse.error("令牌无效或已过期"));
            }

            // 2. 从令牌中获取用户ID
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                logger.warn("无法从令牌中获取用户ID");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(UpdateProfileResponse.error("无法获取用户信息"));
            }

            logger.info("用户ID: {} 正在更新资料", userId);

            // 3. 获取用户信息
            User user = userService.getById(userId);
            if (user == null) {
                logger.warn("用户不存在，用户ID: {}", userId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(UpdateProfileResponse.error("用户不存在"));
            }

            // 4. 更新允许修改的字段
            boolean hasChanges = false;
            
            if (request.getFullName() != null && !request.getFullName().equals(user.getFullName())) {
                logger.debug("更新全名: {} -> {}", user.getFullName(), request.getFullName());
                user.setFullName(request.getFullName());
                hasChanges = true;
            }
            
            if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
                // 检查邮箱是否已被使用
                User existingUser = userService.getUserByEmail(request.getEmail());
                if (existingUser != null && !existingUser.getUserId().equals(user.getUserId())) {
                    logger.warn("邮箱已被使用: {}", request.getEmail());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(UpdateProfileResponse.error("邮箱已被使用"));
                }
                logger.debug("更新邮箱: {} -> {}", user.getEmail(), request.getEmail());
                user.setEmail(request.getEmail());
                hasChanges = true;
            }
            
            if (request.getPhone() != null && !request.getPhone().equals(user.getPhone())) {
                logger.debug("更新手机号");
                user.setPhone(request.getPhone());
                hasChanges = true;
            }
            
            if (request.getGender() != null && !request.getGender().equals(user.getGender())) {
                logger.debug("更新性别: {} -> {}", user.getGender(), request.getGender());
                user.setGender(request.getGender());
                hasChanges = true;
            }
            
            if (request.getBirthday() != null && !request.getBirthday().equals(user.getBirthday())) {
                logger.debug("更新生日");
                user.setBirthday(request.getBirthday());
                hasChanges = true;
            }
            
            if (request.getAvatar() != null && !request.getAvatar().equals(user.getAvatar())) {
                logger.debug("更新头像");
                user.setAvatar(request.getAvatar());
                hasChanges = true;
            }

            // 5. 如果没有任何改变，直接返回
            if (!hasChanges) {
                logger.info("没有需要更新的内容，用户ID: {}", userId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(UpdateProfileResponse.error("没有需要更新的内容"));
            }

            // 6. 执行更新
            boolean success = userService.updateById(user);
            if (!success) {
                logger.error("资料更新失败，用户ID: {}", userId);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(UpdateProfileResponse.error("资料更新失败"));
            }

            logger.info("用户资料更新成功，用户ID: {}", userId);

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
            logger.error("更新用户资料时发生异常", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(UpdateProfileResponse.error("系统错误: " + e.getMessage()));
        }
    }

    /**
     * 修改密码
     * @param authorization JWT令牌（格式：Bearer {token}）
     * @param request 修改密码请求
     * @return 修改结果
     */
    @Operation(summary = "修改密码", description = "修改当前登录用户的密码，需要JWT认证")
    @PutMapping("/password")
    public ResponseEntity<ChangePasswordResponse> changePassword(
            @Parameter(description = "JWT令牌", required = true)
            @RequestHeader("Authorization") String authorization,
            @Parameter(description = "修改密码请求", required = true)
            @Valid @RequestBody ChangePasswordRequest request) {

        logger.info("接收到修改密码请求");

        try {
            // 1. 验证并解析 JWT 令牌
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                logger.warn("修改密码 - 无效的令牌格式");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ChangePasswordResponse.error("无效的令牌格式"));
            }

            String token = authorization.substring(7);

            if (!jwtUtil.validateToken(token)) {
                logger.warn("修改密码 - 令牌无效或已过期");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ChangePasswordResponse.error("令牌无效或已过期"));
            }

            // 2. 从令牌中获取用户ID
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                logger.warn("修改密码 - 无法从令牌中获取用户ID");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ChangePasswordResponse.error("无法获取用户信息"));
            }

            logger.info("用户ID: {} 正在修改密码", userId);

            // 3. 验证两次密码是否一致
            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                logger.warn("用户ID: {} - 两次输入的密码不一致", userId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ChangePasswordResponse.error("两次输入的密码不一致"));
            }

            // 4. 调用服务层修改密码
            Map<String, Object> result = userService.changePassword(
                    userId,
                    request.getOldPassword(),
                    request.getNewPassword()
            );

            boolean success = (boolean) result.get("success");
            String message = (String) result.get("message");

            if (success) {
                logger.info("用户ID: {} 密码修改成功", userId);
                return ResponseEntity.ok(ChangePasswordResponse.success(message));
            } else {
                logger.warn("用户ID: {} 密码修改失败: {}", userId, message);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ChangePasswordResponse.error(message));
            }

        } catch (Exception e) {
            logger.error("修改密码时发生异常", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ChangePasswordResponse.error("系统错误: " + e.getMessage()));
        }
    }

    /**
     * 修改用户名
     * @param authorization JWT令牌（格式：Bearer {token}）
     * @param request 修改用户名请求
     * @return 修改结果和新的token
     */
    @Operation(summary = "修改用户名", description = "修改当前登录用户的用户名，需要JWT认证")
    @PutMapping("/username")
    public ResponseEntity<ChangeUsernameResponse> changeUsername(
            @Parameter(description = "JWT令牌", required = true)
            @RequestHeader("Authorization") String authorization,
            @Parameter(description = "修改用户名请求", required = true)
            @Valid @RequestBody ChangeUsernameRequest request) {

        logger.info("接收到修改用户名请求: {}", request.getUsername());

        try {
            // 1. 验证并解析 JWT 令牌
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                logger.warn("修改用户名 - 无效的令牌格式");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ChangeUsernameResponse.error("无效的令牌格式"));
            }

            String token = authorization.substring(7);

            if (!jwtUtil.validateToken(token)) {
                logger.warn("修改用户名 - 令牌无效或已过期");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ChangeUsernameResponse.error("令牌无效或已过期"));
            }

            // 2. 从令牌中获取用户ID
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                logger.warn("修改用户名 - 无法从令牌中获取用户ID");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ChangeUsernameResponse.error("无法获取用户信息"));
            }

            logger.info("用户ID: {} 正在修改用户名为: {}", userId, request.getUsername());

            // 3. 调用服务层修改用户名
            Map<String, Object> result = userService.changeUsername(userId, request.getUsername());

            boolean success = (boolean) result.get("success");
            String message = (String) result.get("message");

            if (success) {
                String newToken = (String) result.get("token");
                String newUsername = (String) result.get("username");
                logger.info("用户ID: {} 用户名修改成功，新用户名: {}", userId, newUsername);
                return ResponseEntity.ok(ChangeUsernameResponse.success(message, newToken, newUsername));
            } else {
                logger.warn("用户ID: {} 用户名修改失败: {}", userId, message);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ChangeUsernameResponse.error(message));
            }

        } catch (Exception e) {
            logger.error("修改用户名时发生异常", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ChangeUsernameResponse.error("系统错误: " + e.getMessage()));
        }
    }

    /**
     * 修改邮箱
     * @param authorization JWT令牌（格式：Bearer {token}）
     * @param request 修改邮箱请求
     * @return 修改结果
     */
    @Operation(summary = "修改邮箱", description = "修改当前登录用户的邮箱，需要JWT认证和邮箱验证码")
    @PutMapping("/email")
    public ResponseEntity<ChangeEmailResponse> changeEmail(
            @Parameter(description = "JWT令牌", required = true)
            @RequestHeader("Authorization") String authorization,
            @Parameter(description = "修改邮箱请求", required = true)
            @Valid @RequestBody ChangeEmailRequest request) {

        logger.info("接收到修改邮箱请求: {}", request.getEmail());

        try {
            // 1. 验证并解析 JWT 令牌
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                logger.warn("修改邮箱 - 无效的令牌格式");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ChangeEmailResponse.error("无效的令牌格式"));
            }

            String token = authorization.substring(7);

            if (!jwtUtil.validateToken(token)) {
                logger.warn("修改邮箱 - 令牌无效或已过期");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ChangeEmailResponse.error("令牌无效或已过期"));
            }

            // 2. 从令牌中获取用户ID
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                logger.warn("修改邮箱 - 无法从令牌中获取用户ID");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ChangeEmailResponse.error("无法获取用户信息"));
            }

            logger.info("用户ID: {} 正在修改邮箱为: {}", userId, request.getEmail());

            // 3. 调用服务层修改邮箱
            Map<String, Object> result = userService.changeEmail(
                    userId,
                    request.getEmail(),
                    request.getCaptcha()
            );

            boolean success = (boolean) result.get("success");
            String message = (String) result.get("message");

            if (success) {
                String newEmail = (String) result.get("email");
                logger.info("用户ID: {} 邮箱修改成功，新邮箱: {}", userId, newEmail);
                return ResponseEntity.ok(ChangeEmailResponse.success(message, newEmail));
            } else {
                logger.warn("用户ID: {} 邮箱修改失败: {}", userId, message);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ChangeEmailResponse.error(message));
            }

        } catch (Exception e) {
            logger.error("修改邮箱时发生异常", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ChangeEmailResponse.error("系统错误: " + e.getMessage()));
        }
    }
}
