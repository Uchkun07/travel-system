package io.github.uchkun07.travelsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户偏好响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户偏好响应")
public class UserPreferenceResponse {

    @Schema(description = "偏好ID")
    private Long preferenceId;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "偏好景点类型ID")
    private Integer preferAttractionTypeId;

    @Schema(description = "景点类型名称")
    private String attractionTypeName;

    @Schema(description = "预算下限")
    private Integer budgetFloor;

    @Schema(description = "预算上限")
    private Integer budgetRange;

    @Schema(description = "出行人群")
    private String travelCrowd;

    @Schema(description = "偏好出行季节")
    private String preferSeason;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
