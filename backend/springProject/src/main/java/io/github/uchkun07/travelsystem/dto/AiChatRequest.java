package io.github.uchkun07.travelsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * AI聊天请求DTO
 */
@Data
@Schema(description = "AI聊天请求")
public class AiChatRequest {

    @Schema(description = "用户问题", required = true, example = "你好，请介绍一下北京有什么旅游景点？")
    private String message;

    @Schema(description = "会话ID（可选，用于多轮对话）", example = "session-12345")
    private String sessionId;
}
