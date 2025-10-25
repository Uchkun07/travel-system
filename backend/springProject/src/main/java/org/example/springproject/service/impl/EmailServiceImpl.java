package org.example.springproject.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.example.springproject.service.IEmailService;
import org.example.springproject.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.security.SecureRandom;

/**
 * 邮件服务实现类
 */
@Slf4j
@Service
public class EmailServiceImpl implements IEmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private RedisUtil redisUtil;

    @Value("${spring.mail.username}")
    private String from;

    // Redis Key 前缀
    private static final String EMAIL_CODE_PREFIX = "email_code:";
    private static final String IP_MINUTE_PREFIX = "ip_minute:";
    private static final String IP_HOUR_PREFIX = "ip_hour:";
    private static final String IP_DAY_PREFIX = "ip_day:";
    private static final String EMAIL_LIMIT_PREFIX = "email_limit:";
    private static final String EMAIL_VERIFIED_PREFIX = "email_verified:";

    // 限制阈值
    private static final int IP_MINUTE_LIMIT = 1;   // 1分钟内最多1次
    private static final int IP_HOUR_LIMIT = 5;     // 1小时内最多5次
    private static final int IP_DAY_LIMIT = 20;     // 1天内最多20次
    private static final int EMAIL_HOUR_LIMIT = 3;  // 同一邮箱1小时内最多3次

    @Override
    public void sendSimpleMail(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            mailSender.send(message);
            log.info("简单邮件发送成功,收件人:{}", to);
        } catch (Exception e) {
            log.error("简单邮件发送失败,收件人:{}, 错误信息:{}", to, e.getMessage());
            throw new RuntimeException("邮件发送失败: " + e.getMessage());
        }
    }

    @Override
    @SuppressWarnings("null")
    public void sendHtmlMail(String to, String subject, String content) {
        try {
            if (from == null || to == null || subject == null || content == null) {
                throw new IllegalArgumentException("邮件参数不能为空");
            }
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
            log.info("HTML邮件发送成功,收件人:{}", to);
        } catch (MessagingException e) {
            log.error("HTML邮件发送失败,收件人:{}, 错误信息:{}", to, e.getMessage());
            throw new RuntimeException("HTML邮件发送失败: " + e.getMessage());
        }
    }

    @Override
    public String sendVerificationCode(String to, String ip) {
        // 1. IP限制检查
        checkIpLimit(ip);

        // 2. 邮箱限制检查
        checkEmailLimit(to);

        // 3. 生成6位验证码
        String code = generateVerificationCode();

        // 4. 发送邮件
        String subject = "旅游系统 - 邮箱验证码";
        String content = buildVerificationCodeHtml(code);
        sendHtmlMail(to, subject, content);

        // 5. 存储验证码到Redis (10分钟有效)
        String codeKey = EMAIL_CODE_PREFIX + to;
        redisUtil.set(codeKey, code, 600);

        // 6. 更新IP限制计数
        updateIpLimit(ip);

        // 7. 更新邮箱限制计数
        updateEmailLimit(to);

        log.info("验证码发送成功,邮箱:{}, IP:{}", to, ip);
        return code;
    }

    @Override
    public boolean verifyCode(String email, String code) {
        if (email == null || code == null) {
            return false;
        }

        String codeKey = EMAIL_CODE_PREFIX + email;
        Object savedCode = redisUtil.get(codeKey);

        if (savedCode == null) {
            log.warn("验证码不存在或已过期,邮箱:{}", email);
            return false;
        }

        if (savedCode.toString().equals(code)) {
            // 验证成功后删除验证码
            redisUtil.del(codeKey);
            
            // 设置验证成功标记(30分钟有效)
            String verifiedKey = EMAIL_VERIFIED_PREFIX + email;
            redisUtil.set(verifiedKey, "1", 1800);
            
            log.info("验证码验证成功,邮箱:{}", email);
            return true;
        } else {
            log.warn("验证码错误,邮箱:{}", email);
            return false;
        }
    }

    @Override
    @SuppressWarnings("null")
    public void sendAttachmentMail(String to, String subject, String content, String filePath) {
        try {
            if (from == null || to == null || subject == null || content == null) {
                throw new IllegalArgumentException("邮件参数不能为空");
            }
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            FileSystemResource file = new FileSystemResource(new File(filePath));
            String fileName = file.getFilename();
            if (fileName != null) {
                helper.addAttachment(fileName, file);
            }

            mailSender.send(message);
            log.info("带附件邮件发送成功,收件人:{}, 附件:{}", to, fileName);
        } catch (MessagingException e) {
            log.error("带附件邮件发送失败,收件人:{}, 错误信息:{}", to, e.getMessage());
            throw new RuntimeException("带附件邮件发送失败: " + e.getMessage());
        }
    }

    /**
     * 检查IP限制
     */
    private void checkIpLimit(String ip) {
        // 检查分钟级限制
        String minuteKey = IP_MINUTE_PREFIX + ip;
        long minuteCount = getCount(minuteKey);
        if (minuteCount >= IP_MINUTE_LIMIT) {
            throw new RuntimeException("发送过于频繁,请1分钟后再试");
        }

        // 检查小时级限制
        String hourKey = IP_HOUR_PREFIX + ip;
        long hourCount = getCount(hourKey);
        if (hourCount >= IP_HOUR_LIMIT) {
            throw new RuntimeException("发送次数过多,请1小时后再试");
        }

        // 检查天级限制
        String dayKey = IP_DAY_PREFIX + ip;
        long dayCount = getCount(dayKey);
        if (dayCount >= IP_DAY_LIMIT) {
            throw new RuntimeException("今日发送次数已达上限,请明天再试");
        }
    }

    /**
     * 检查邮箱限制
     */
    private void checkEmailLimit(String email) {
        String emailKey = EMAIL_LIMIT_PREFIX + email;
        long count = getCount(emailKey);
        if (count >= EMAIL_HOUR_LIMIT) {
            throw new RuntimeException("该邮箱请求过于频繁,请1小时后再试");
        }
    }

    /**
     * 更新IP限制计数
     */
    private void updateIpLimit(String ip) {
        // 分钟级计数
        String minuteKey = IP_MINUTE_PREFIX + ip;
        incrementAndExpire(minuteKey, 60);

        // 小时级计数
        String hourKey = IP_HOUR_PREFIX + ip;
        incrementAndExpire(hourKey, 3600);

        // 天级计数
        String dayKey = IP_DAY_PREFIX + ip;
        incrementAndExpire(dayKey, 86400);
    }

    /**
     * 更新邮箱限制计数
     */
    private void updateEmailLimit(String email) {
        String emailKey = EMAIL_LIMIT_PREFIX + email;
        incrementAndExpire(emailKey, 3600);
    }

    /**
     * 获取计数
     */
    private long getCount(String key) {
        Object value = redisUtil.get(key);
        if (value == null) {
            return 0;
        }
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * 递增并设置过期时间
     */
    private void incrementAndExpire(String key, long seconds) {
        if (!redisUtil.hasKey(key)) {
            redisUtil.set(key, "1", seconds);
        } else {
            redisUtil.incr(key, 1);
        }
    }

    /**
     * 生成6位数字验证码
     * 使用 SecureRandom 提供密码学安全的随机数生成
     */
    private String generateVerificationCode() {
        SecureRandom secureRandom = new SecureRandom();
        int code = secureRandom.nextInt(900000) + 100000;
        return String.valueOf(code);
    }

    /**
     * 构建验证码邮件HTML内容
     */
    private String buildVerificationCodeHtml(String code) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "    <meta charset='UTF-8'>" +
                "    <style>" +
                "        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                "        .container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                "        .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }" +
                "        .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }" +
                "        .code-box { background: white; border: 2px dashed #667eea; border-radius: 8px; padding: 20px; text-align: center; margin: 20px 0; }" +
                "        .code { font-size: 32px; font-weight: bold; color: #667eea; letter-spacing: 5px; }" +
                "        .footer { text-align: center; margin-top: 20px; color: #999; font-size: 12px; }" +
                "        .tips { background: #fff3cd; border-left: 4px solid #ffc107; padding: 10px; margin: 15px 0; }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class='container'>" +
                "        <div class='header'>" +
                "            <h1>🌏 悦旅</h1>" +
                "            <p>邮箱验证码</p>" +
                "        </div>" +
                "        <div class='content'>" +
                "            <p>您好!</p>" +
                "            <p>您正在进行邮箱验证,您的验证码是:</p>" +
                "            <div class='code-box'>" +
                "                <div class='code'>" + code + "</div>" +
                "            </div>" +
                "            <div class='tips'>" +
                "                <strong>⚠️ 温馨提示:</strong>" +
                "                <ul style='margin: 10px 0; padding-left: 20px;'>" +
                "                    <li>验证码有效期为 <strong>10分钟</strong></li>" +
                "                    <li>请勿将验证码泄露给他人</li>" +
                "                    <li>如非本人操作,请忽略此邮件</li>" +
                "                </ul>" +
                "            </div>" +
                "            <p>感谢您使用我们的服务!</p>" +
                "        </div>" +
                "        <div class='footer'>" +
                "            <p>此邮件由系统自动发送,请勿直接回复</p>" +
                "            <p>&copy; 2025 旅游系统. All rights reserved.</p>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";
    }
}
