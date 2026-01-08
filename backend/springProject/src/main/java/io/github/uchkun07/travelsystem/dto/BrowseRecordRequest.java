package io.github.uchkun07.travelsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 浏览记录请求DTO
 */
@Data
@Schema(description = "浏览记录埋点请求")
public class BrowseRecordRequest {

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", required = true)
    private Long userId;

    @NotNull(message = "景点ID不能为空")
    @Schema(description = "景点ID", required = true)
    private Long attractionId;

    @NotNull(message = "浏览时长不能为空")
    @Min(value = 0, message = "浏览时长不能为负数")
    @Schema(description = "浏览时长（秒）", required = true, example = "30")
    private Integer browseDuration;

    @Schema(description = "设备信息（可选）", example = "Chrome/Windows 10")
    private String deviceInfo;
}
