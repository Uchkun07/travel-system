package io.github.uchkun07.travelsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 景点标签请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "景点标签请求")
public class AttractionTagRequest {

    @Schema(description = "标签ID(修改时必填)")
    private Integer tagId;

    @Schema(description = "标签名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String tagName;

    @Schema(description = "标签描述")
    private String description;

    @Schema(description = "排序序号")
    private Integer sortOrder;

    @Schema(description = "状态(1=启用,0=禁用)")
    private Integer status;
}
