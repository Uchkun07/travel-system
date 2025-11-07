package org.example.springproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.springproject.config.FileUploadProperties;
import org.example.springproject.entity.User;
import org.example.springproject.dto.ApiResponse;
import org.example.springproject.service.IUserService;
import org.example.springproject.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传控制器
 */
@Tag(name = "文件上传", description = "文件上传相关操作（需要JWT认证）")
@RestController
@RequestMapping("/v1/upload")
public class FileUploadController {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    @Autowired
    private IUserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private FileUploadProperties fileUploadProperties;

    // 允许的图片格式
    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif", ".webp"};
    
    // 最大文件大小：5MB
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    /**
     * 上传头像
     * @param authorization JWT令牌（格式：Bearer {token}）
     * @param file 头像文件
     * @return 上传结果，包含头像URL和新token
     */
    @Operation(summary = "上传头像", description = "上传用户头像图片，需要JWT认证")
    @PostMapping("/avatar")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadAvatar(
            @Parameter(description = "JWT令牌", required = true)
            @RequestHeader("Authorization") String authorization,
            @Parameter(description = "头像文件", required = true)
            @RequestParam("file") MultipartFile file) {

        logger.info("接收到头像上传请求，文件名: {}, 大小: {} bytes", 
                    file.getOriginalFilename(), file.getSize());

        try {
            // 1. 验证并解析 JWT 令牌
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                logger.warn("无效的令牌格式");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(401, "无效的令牌格式"));
            }

            String token = authorization.substring(7);
            
            if (!jwtUtil.validateToken(token)) {
                logger.warn("令牌无效或已过期");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(401, "令牌无效或已过期"));
            }

            // 2. 从令牌中获取用户ID
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                logger.warn("无法从令牌中获取用户ID");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(401, "无法获取用户信息"));
            }

            // 3. 验证文件
            if (file.isEmpty()) {
                logger.warn("用户ID: {} - 上传的文件为空", userId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(400, "请选择要上传的文件"));
            }

            // 验证文件大小
            if (file.getSize() > MAX_FILE_SIZE) {
                logger.warn("用户ID: {} - 文件大小超过限制: {} bytes", userId, file.getSize());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(400, "文件大小不能超过5MB"));
            }

            // 验证文件类型
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !isValidImageFile(originalFilename)) {
                logger.warn("用户ID: {} - 不支持的文件类型: {}", userId, originalFilename);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(400, "只支持 jpg、jpeg、png、gif、webp 格式的图片"));
            }

            // 4. 生成新文件名（使用UUID避免重复）
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFileName = UUID.randomUUID().toString() + fileExtension;
            
            // 按日期创建子目录
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String dateDir = sdf.format(new Date());
            
            // 5. 创建上传目录
            String uploadPath = fileUploadProperties.getPath();
            Path uploadDir = Paths.get(uploadPath, dateDir);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
                logger.info("创建上传目录: {}", uploadDir);
            }

            // 6. 保存文件
            Path filePath = uploadDir.resolve(newFileName);
            Files.copy(file.getInputStream(), filePath);
            logger.info("用户ID: {} - 文件保存成功: {}", userId, filePath);

            // 7. 生成访问URL（相对路径）
            String avatarUrl = "/img/avatars/" + dateDir + "/" + newFileName;
            logger.info("用户ID: {} - 头像URL: {}", userId, avatarUrl);

            // 8. 更新用户头像信息
            User user = userService.getById(userId);
            if (user == null) {
                logger.error("用户ID: {} - 用户不存在", userId);
                // 删除已上传的文件
                Files.deleteIfExists(filePath);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(400, "用户不存在"));
            }

            // 删除旧头像文件（如果存在且不是默认头像）
            if (user.getAvatar() != null && !user.getAvatar().contains("default")) {
                try {
                    String oldAvatarUrl = user.getAvatar();
                    if (oldAvatarUrl.startsWith("/img/avatars/")) {
                        String relativePath = oldAvatarUrl.substring("/img/avatars/".length());
                        Path oldFilePath = Paths.get(fileUploadProperties.getPath(), relativePath);
                        if (Files.exists(oldFilePath)) {
                            Files.delete(oldFilePath);
                            logger.info("删除旧头像文件: {}", oldFilePath);
                        }
                    }
                } catch (Exception e) {
                    logger.warn("删除旧头像文件失败: {}", e.getMessage());
                }
            }

            user.setAvatar(avatarUrl);
            boolean success = userService.updateById(user);
            
            if (!success) {
                logger.error("用户ID: {} - 更新头像失败", userId);
                // 删除已上传的文件
                Files.deleteIfExists(filePath);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.error(500, "更新头像失败"));
            }

            // 9. 生成新的JWT令牌（包含更新后的头像）
            String newToken = jwtUtil.generateTokenWithUserInfo(
                    user.getUserId().longValue(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getFullName(),
                    user.getAvatar(),
                    user.getPhone(),
                    user.getGender(),
                    user.getBirthday(),
                    true
            );

            // 10. 返回成功响应
            Map<String, String> data = new HashMap<>();
            data.put("avatarUrl", avatarUrl);
            data.put("token", newToken);
            
            logger.info("用户ID: {} - 头像上传成功", userId);
            return ResponseEntity.ok(ApiResponse.success("头像上传成功", data));

        } catch (IOException e) {
            logger.error("文件上传失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "文件上传失败: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("头像上传时发生异常", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "系统错误: " + e.getMessage()));
        }
    }

    /**
     * 验证是否为有效的图片文件
     */
    private boolean isValidImageFile(String filename) {
        String lowerCaseFilename = filename.toLowerCase();
        for (String ext : ALLOWED_EXTENSIONS) {
            if (lowerCaseFilename.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }
}
