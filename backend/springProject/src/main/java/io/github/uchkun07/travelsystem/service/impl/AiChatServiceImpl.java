package io.github.uchkun07.travelsystem.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.uchkun07.travelsystem.config.DeepSeekConfig;
import io.github.uchkun07.travelsystem.dto.AiMessage;
import io.github.uchkun07.travelsystem.dto.DeepSeekRequest;
import io.github.uchkun07.travelsystem.dto.DeepSeekStreamResponse;
import io.github.uchkun07.travelsystem.service.IAiChatService;
import io.github.uchkun07.travelsystem.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * AI聊天服务实现类
 */
@Slf4j
@Service
public class AiChatServiceImpl implements IAiChatService {

    @Autowired
    private DeepSeekConfig deepSeekConfig;

    @Autowired
    private RedisUtil redisUtil;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String REDIS_SESSION_PREFIX = "ai:session:";
    private static final int SESSION_EXPIRE_HOURS = 24;

    @Override
    public SseEmitter streamChat(String message, String sessionId) {
        // 创建SSE emitter，设置超时时间
        SseEmitter emitter = new SseEmitter(300000L); // 5分钟超时

        // 如果没有sessionId，生成一个新的
        if (sessionId == null || sessionId.isEmpty()) {
            sessionId = "session-" + UUID.randomUUID().toString();
        }

        final String finalSessionId = sessionId;

        // 获取会话历史
        List<AiMessage> messages = getSessionMessages(finalSessionId);

        // 添加系统提示词（如果是新会话）
        if (messages.isEmpty()) {
            messages.add(new AiMessage("system", "你是一个专业的旅游助手，可以为用户推荐旅游景点、规划行程、解答旅游相关问题。"));
        }

        // 添加用户消息
        messages.add(new AiMessage("user", message));

        // 构建DeepSeek请求
        DeepSeekRequest request = DeepSeekRequest.builder()
                .model(deepSeekConfig.getModel())
                .messages(messages)
                .stream(true)
                .temperature(0.7)
                .build();

        // 创建OkHttp客户端
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(deepSeekConfig.getTimeout(), TimeUnit.SECONDS)
                .readTimeout(deepSeekConfig.getTimeout(), TimeUnit.SECONDS)
                .writeTimeout(deepSeekConfig.getTimeout(), TimeUnit.SECONDS)
                .build();

        try {
            // 构建HTTP请求
            RequestBody body = RequestBody.create(
                    objectMapper.writeValueAsString(request),
                    MediaType.parse("application/json; charset=utf-8")
            );

            Request httpRequest = new Request.Builder()
                    .url(deepSeekConfig.getUrl())
                    .addHeader("Authorization", "Bearer " + deepSeekConfig.getKey())
                    .addHeader("Accept", "text/event-stream")
                    .post(body)
                    .build();

            // 用于累积完整回复
            StringBuilder fullResponse = new StringBuilder();

            // 创建EventSource监听器
            EventSourceListener listener = new EventSourceListener() {
                @Override
                public void onOpen(EventSource eventSource, Response response) {
                    log.info("DeepSeek SSE连接已建立，会话ID: {}", finalSessionId);
                    try {
                        // 发送会话ID给客户端
                        emitter.send(SseEmitter.event()
                                .name("session")
                                .data("{\"sessionId\":\"" + finalSessionId + "\"}"));
                    } catch (IOException e) {
                        log.error("发送会话ID失败", e);
                    }
                }

                @Override
                public void onEvent(EventSource eventSource, String id, String type, String data) {
                    try {
                        // DeepSeek发送[DONE]表示流结束
                        if ("[DONE]".equals(data)) {
                            log.info("DeepSeek流式响应完成");
                            
                            // 保存助手回复到会话历史
                            messages.add(new AiMessage("assistant", fullResponse.toString()));
                            saveSessionMessages(finalSessionId, messages);
                            
                            // 发送完成事件
                            emitter.send(SseEmitter.event()
                                    .name("done")
                                    .data("{\"message\":\"完成\"}"));
                            emitter.complete();
                            return;
                        }

                        // 解析DeepSeek响应
                        DeepSeekStreamResponse streamResponse = objectMapper.readValue(data, DeepSeekStreamResponse.class);

                        if (streamResponse.getChoices() != null && !streamResponse.getChoices().isEmpty()) {
                            DeepSeekStreamResponse.Delta delta = streamResponse.getChoices().get(0).getDelta();
                            if (delta != null && delta.getContent() != null) {
                                String content = delta.getContent();
                                fullResponse.append(content);

                                // 发送内容块给客户端
                                emitter.send(SseEmitter.event()
                                        .name("message")
                                        .data(content));
                            }
                        }
                    } catch (Exception e) {
                        log.error("处理SSE事件失败", e);
                        try {
                            emitter.send(SseEmitter.event()
                                    .name("error")
                                    .data("{\"error\":\"处理响应失败: " + e.getMessage() + "\"}"));
                            emitter.completeWithError(e);
                        } catch (IOException ioException) {
                            log.error("发送错误事件失败", ioException);
                        }
                    }
                }

                @Override
                public void onClosed(EventSource eventSource) {
                    log.info("DeepSeek SSE连接已关闭");
                    emitter.complete();
                }

                @Override
                public void onFailure(EventSource eventSource, Throwable t, Response response) {
                    log.error("DeepSeek SSE连接失败", t);
                    try {
                        String errorMsg = t.getMessage();
                        if (response != null) {
                            errorMsg = "HTTP " + response.code() + ": " + response.message();
                        }
                        emitter.send(SseEmitter.event()
                                .name("error")
                                .data("{\"error\":\"" + errorMsg + "\"}"));
                        emitter.completeWithError(t);
                    } catch (IOException e) {
                        log.error("发送错误事件失败", e);
                        emitter.completeWithError(e);
                    }
                }
            };

            // 创建EventSource
            EventSource eventSource = EventSources.createFactory(client)
                    .newEventSource(httpRequest, listener);

            // 处理emitter的超时和错误
            emitter.onTimeout(() -> {
                log.warn("SSE连接超时，会话ID: {}", finalSessionId);
                eventSource.cancel();
            });

            emitter.onError((e) -> {
                log.error("SSE连接错误，会话ID: {}", finalSessionId, e);
                eventSource.cancel();
            });

            emitter.onCompletion(() -> {
                log.info("SSE连接完成，会话ID: {}", finalSessionId);
                eventSource.cancel();
            });

        } catch (Exception e) {
            log.error("创建SSE连接失败", e);
            try {
                emitter.send(SseEmitter.event()
                        .name("error")
                        .data("{\"error\":\"创建连接失败: " + e.getMessage() + "\"}"));
                emitter.completeWithError(e);
            } catch (IOException ioException) {
                log.error("发送错误事件失败", ioException);
            }
        }

        return emitter;
    }

    @Override
    public void clearSession(String sessionId) {
        if (sessionId != null && !sessionId.isEmpty()) {
            String key = REDIS_SESSION_PREFIX + sessionId;
            redisUtil.del(key);
            log.info("已清空会话: {}", sessionId);
        }
    }

    /**
     * 获取会话消息历史
     */
    @SuppressWarnings("unchecked")
    private List<AiMessage> getSessionMessages(String sessionId) {
        String key = REDIS_SESSION_PREFIX + sessionId;
        Object obj = redisUtil.get(key);
        if (obj != null) {
            try {
                return (List<AiMessage>) obj;
            } catch (Exception e) {
                log.error("解析会话历史失败", e);
            }
        }
        return new ArrayList<>();
    }

    /**
     * 保存会话消息历史
     */
    private void saveSessionMessages(String sessionId, List<AiMessage> messages) {
        String key = REDIS_SESSION_PREFIX + sessionId;
        // 只保留最近10轮对话（20条消息 + 1条系统提示）
        int maxSize = 21;
        if (messages.size() > maxSize) {
            // 保留系统提示词
            AiMessage systemMessage = messages.get(0);
            List<AiMessage> recentMessages = new ArrayList<>();
            recentMessages.add(systemMessage);
            recentMessages.addAll(messages.subList(messages.size() - (maxSize - 1), messages.size()));
            messages = recentMessages;
        }
        redisUtil.set(key, messages, SESSION_EXPIRE_HOURS * 3600);
    }
}
