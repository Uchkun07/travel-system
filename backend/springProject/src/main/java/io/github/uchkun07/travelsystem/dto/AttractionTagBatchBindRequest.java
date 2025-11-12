package io.github.uchkun07.travelsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 景点批量绑定标签请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "景点批量绑定标签请求")
public class AttractionTagBatchBindRequest {

    @Schema(description = "景点ID")
    private Long attractionId;

    @Schema(description = "标签ID列表")
    private List<Integer> tagIds;
}
