package io.github.uchkun07.travelsystem.service;

/**
 * 邮件服务接口
 */
public interface IEmailService {

    /**
     * 发送简单文本邮件
     *
     * @param to      收件人邮箱
     * @param subject 邮件主题
     * @param content 邮件内容
     */
    void sendSimpleMail(String to, String subject, String content);

    /**
     * 发送HTML邮件
     *
     * @param to      收件人邮箱
     * @param subject 邮件主题
     * @param content HTML内容
     */
    void sendHtmlMail(String to, String subject, String content);

    /**
     * 发送验证码邮件(带IP限制检查)
     *
     * @param to 收件人邮箱
     * @param ip 发送者IP
     * @return 验证码
     */
    String sendVerificationCode(String to, String ip);

    /**
     * 验证邮箱验证码
     *
     * @param email 邮箱
     * @param code  验证码
     * @return 是否验证成功
     */
    boolean verifyCode(String email, String code);

    /**
     * 发送带附件的邮件
     *
     * @param to       收件人邮箱
     * @param subject  邮件主题
     * @param content  邮件内容
     * @param filePath 附件路径
     */
    void sendAttachmentMail(String to, String subject, String content, String filePath);
}
