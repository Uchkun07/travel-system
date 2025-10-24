package org.example.springproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.springproject.entity.dto.SendCodeRequest;
import org.example.springproject.service.IEmailService;
import org.example.springproject.util.IpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 邮件控制器
 */
@Slf4j
@Tag(name = "邮件管理", description = "邮件发送相关接口")
@RestController
@RequestMapping("/email")
public class EmailController {

    @Autowired
    private IEmailService emailService;

    /**
     * 发送验证码
     */
    @Operation(summary = "发送验证码", description = "发送邮箱验证码")
    @PostMapping("/sendCode")
    public Map<String, Object> sendVerificationCode(
            @RequestBody SendCodeRequest request,
            HttpServletRequest httpRequest) {
        Map<String, Object> result = new HashMap<>();

        try {
            String email = request.getEmail();

            // 验证邮箱格式
            if (!isValidEmail(email)) {
                result.put("success", false);
                result.put("message", "邮箱格式不正确");
                return result;
            }

            // 获取客户端IP
            String ip = IpUtil.getClientIp(httpRequest);

            // 发送验证码(包含IP和邮箱限制检查)
            emailService.sendVerificationCode(email, ip);

            result.put("success", true);
            result.put("message", "验证码已发送,请查收邮件");

        } catch (RuntimeException e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            log.warn("验证码发送被限制:{}", e.getMessage());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "验证码发送失败,请稍后重试");
            log.error("验证码发送异常", e);
        }

        return result;
    }

    /**
     * 发送简单文本邮件(测试用)
     */
    @Operation(summary = "发送简单邮件", description = "发送简单文本邮件(测试用)")
    @PostMapping("/sendSimple")
    public Map<String, Object> sendSimpleMail(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String content) {
        Map<String, Object> result = new HashMap<>();

        try {
            emailService.sendSimpleMail(to, subject, content);
            result.put("success", true);
            result.put("message", "邮件发送成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "邮件发送失败: " + e.getMessage());
        }

        return result;
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
