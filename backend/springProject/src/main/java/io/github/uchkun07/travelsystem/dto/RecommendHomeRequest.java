package io.github.uchkun07.travelsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 首页推荐请求
 */
@Data
@Schema(description = "首页推荐请求")
public class RecommendHomeRequest {

    @Schema(description = "页码", example = "1")
    private Long pageNum = 1L;

    @Schema(description = "每页大小", example = "20")
    private Long pageSize = 20L;
}
