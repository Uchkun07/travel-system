package io.github.uchkun07.travelsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 首页推荐响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "首页推荐响应")
public class RecommendHomeResponse {

    @Schema(description = "推荐请求ID")
    private String requestId;

    @Schema(description = "推荐版本")
    private String recVersion;

    @Schema(description = "是否启用行为画像")
    private Boolean behaviorEnabled;

    @Schema(description = "推荐结果分页")
    private PageResponse<AttractionCardResponse> page;
}
