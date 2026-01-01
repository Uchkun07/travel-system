package io.github.uchkun07.travelsystem.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * AI聊天服务接口
 */
public interface IAiChatService {

    /**
     * 流式对话（SSE）
     *
     * @param message   用户消息
     * @param sessionId 会话ID（可选）
     * @return SseEmitter 流式响应对象
     */
    SseEmitter streamChat(String message, String sessionId);

    /**
     * 清空会话历史
     *
     * @param sessionId 会话ID
     */
    void clearSession(String sessionId);
}
