package io.github.uchkun07.travelsystem.controller;

import io.github.uchkun07.travelsystem.dto.AiChatRequest;
import io.github.uchkun07.travelsystem.dto.ApiResponse;
import io.github.uchkun07.travelsystem.service.IAiChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * AI聊天控制器
 */
@Slf4j
@Tag(name = "AI聊天助手", description = "基于DeepSeek的AI旅游问答接口")
@RestController
@RequestMapping("/api/ai/chat")
public class AiChatController {

    @Autowired
    private IAiChatService aiChatService;

    /**
     * 流式对话接口（SSE） - POST方式
     */
    @Operation(summary = "AI流式对话(POST)", description = "使用Server-Sent Events实现实时流式响应")
    @PostMapping(value = "/stream", produces = "text/event-stream")
    public SseEmitter streamChatPost(@RequestBody AiChatRequest request) {
        log.info("收到AI聊天请求(POST)，消息: {}, 会话ID: {}", request.getMessage(), request.getSessionId());

        if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
            SseEmitter emitter = new SseEmitter();
            try {
                emitter.send(SseEmitter.event()
                        .name("error")
                        .data("{\"error\":\"消息不能为空\"}"));
                emitter.complete();
            } catch (Exception e) {
                log.error("发送错误消息失败", e);
            }
            return emitter;
        }

        return aiChatService.streamChat(request.getMessage(), request.getSessionId());
    }

    /**
     * 流式对话接口（SSE） - GET方式（用于EventSource）
     */
    @Operation(summary = "AI流式对话(GET)", description = "使用Server-Sent Events实现实时流式响应，支持EventSource")
    @GetMapping(value = "/stream", produces = "text/event-stream")
    public SseEmitter streamChatGet(
            @Parameter(description = "用户消息", required = true)
            @RequestParam String message,
            @Parameter(description = "会话ID（可选）")
            @RequestParam(required = false) String sessionId) {
        log.info("收到AI聊天请求(GET)，消息: {}, 会话ID: {}", message, sessionId);

        if (message == null || message.trim().isEmpty()) {
            SseEmitter emitter = new SseEmitter();
            try {
                emitter.send(SseEmitter.event()
                        .name("error")
                        .data("{\"error\":\"消息不能为空\"}"));
                emitter.complete();
            } catch (Exception e) {
                log.error("发送错误消息失败", e);
            }
            return emitter;
        }

        return aiChatService.streamChat(message, sessionId);
    }

    /**
     * 清空会话历史
     */
    @Operation(summary = "清空会话历史", description = "删除指定会话的所有历史记录")
    @DeleteMapping("/session/{sessionId}")
    public ApiResponse<String> clearSession(
            @Parameter(description = "会话ID", required = true)
            @PathVariable String sessionId) {
        log.info("清空会话: {}", sessionId);
        aiChatService.clearSession(sessionId);
        return ApiResponse.success("会话已清空");
    }

    /**
     * 健康检查接口
     */
    @Operation(summary = "健康检查", description = "检查AI服务是否正常运行")
    @GetMapping("/health")
    public ApiResponse<String> health() {
        return ApiResponse.success("AI服务运行正常");
    }
}
