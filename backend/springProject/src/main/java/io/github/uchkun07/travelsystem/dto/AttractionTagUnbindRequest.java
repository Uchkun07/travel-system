package io.github.uchkun07.travelsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 景点解绑标签请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "景点解绑标签请求")
public class AttractionTagUnbindRequest {

    @Schema(description = "景点ID")
    private Long attractionId;

    @Schema(description = "标签ID")
    private Integer tagId;
}
