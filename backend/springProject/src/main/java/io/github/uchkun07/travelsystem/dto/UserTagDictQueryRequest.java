package io.github.uchkun07.travelsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户标签字典查询请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户标签字典查询请求")
public class UserTagDictQueryRequest {

    @Builder.Default
    @Schema(description = "页码", defaultValue = "1")
    private Long pageNum = 1L;

    @Builder.Default
    @Schema(description = "每页大小", defaultValue = "10")
    private Long pageSize = 10L;

    @Schema(description = "标签名称(模糊查询)")
    private String tagName;

    @Schema(description = "标签编码")
    private String tagCode;

    @Schema(description = "标签等级")
    private Integer tagLevel;

    @Schema(description = "标签状态")
    private Integer status;
}
