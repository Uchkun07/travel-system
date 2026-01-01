package io.github.uchkun07.travelsystem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DeepSeek API 请求体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeepSeekRequest {

    @JsonProperty("model")
    private String model;

    @JsonProperty("messages")
    private List<AiMessage> messages;

    @JsonProperty("stream")
    private Boolean stream;

    @JsonProperty("temperature")
    private Double temperature; // 0-2, 默认1

    @JsonProperty("max_tokens")
    private Integer maxTokens;
}
