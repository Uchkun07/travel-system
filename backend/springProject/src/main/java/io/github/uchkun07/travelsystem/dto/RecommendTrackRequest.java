package io.github.uchkun07.travelsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 推荐埋点请求
 */
@Data
@Schema(description = "推荐埋点请求")
public class RecommendTrackRequest {

    @NotNull
    @Schema(description = "景点ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long attractionId;

    @NotBlank
    @Schema(description = "事件类型: impression/click/stay", requiredMode = Schema.RequiredMode.REQUIRED)
    private String eventType;

    @Schema(description = "推荐请求ID")
    private String requestId;

    @Min(1)
    @Schema(description = "推荐位次")
    private Integer position;

    @Min(0)
    @Schema(description = "停留秒数（stay事件使用）")
    private Integer staySeconds;

    @Schema(description = "来源页面，如 home/detail")
    private String sourcePage;

    @Schema(description = "推荐版本")
    private String recVersion;
}
