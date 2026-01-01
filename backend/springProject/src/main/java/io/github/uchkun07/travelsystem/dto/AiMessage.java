package io.github.uchkun07.travelsystem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI消息对象（用于构建请求）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiMessage {

    @JsonProperty("role")
    private String role; // "system" | "user" | "assistant"

    @JsonProperty("content")
    private String content;
}
