package io.github.uchkun07.travelsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户偏好请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户偏好请求")
public class UserPreferenceRequest {

    @Schema(description = "偏好景点类型ID")
    private Integer preferAttractionTypeId;

    @Schema(description = "预算下限", example = "100")
    private Integer budgetFloor;

    @Schema(description = "预算上限", example = "5000")
    private Integer budgetRange;

    @Schema(description = "出行人群（如独自出行、情侣、亲子）", example = "情侣")
    private String travelCrowd;

    @Schema(description = "偏好出行季节（如春、夏、秋、冬）", example = "春")
    private String preferSeason;
}
