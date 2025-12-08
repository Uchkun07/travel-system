package io.github.uchkun07.travelsystem.controller;

import io.github.uchkun07.travelsystem.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import io.github.uchkun07.travelsystem.dto.SendCodeRequest;
import io.github.uchkun07.travelsystem.service.IEmailService;
import io.github.uchkun07.travelsystem.util.IpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 邮件控制器
 */
@Slf4j
@Tag(name = "邮件", description = "邮件发送相关接口")
@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private IEmailService emailService;

    /**
     * 发送验证码
     */
    @Operation(summary = "发送验证码", description = "发送邮箱验证码")
    @PostMapping("/sendCode")
    public ApiResponse<String> sendVerificationCode(
            @RequestBody SendCodeRequest request,
            HttpServletRequest httpRequest) {

        try {
            String email = request.getEmail();
            log.info("收到验证码发送请求,邮箱:{}", email);

            // 验证邮箱格式
            if (!isValidEmail(email)) {
                log.warn("邮箱格式不正确:{}", email);
                return ApiResponse.error(400, "邮箱格式不正确");
            }

            // 获取客户端IP
            String ip = IpUtil.getClientIp(httpRequest);
            log.info("客户端IP:{}", ip);

            // 发送验证码(包含IP和邮箱限制检查)
            emailService.sendVerificationCode(email, ip);

            log.info("验证码发送成功,邮箱:{}", email);
            return ApiResponse.success("验证码已发送,请查收邮件", null);

        } catch (RuntimeException e) {
            log.error("验证码发送被限制,RuntimeException: {}", e.getMessage(), e);
            return ApiResponse.error(429, e.getMessage());
        } catch (Exception e) {
            log.error("验证码发送异常,Exception: {}", e.getMessage(), e);
            return ApiResponse.error(500, "验证码发送失败: " + e.getMessage());
        }
    }

    /**
     * 验证邮箱格式
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }
}
