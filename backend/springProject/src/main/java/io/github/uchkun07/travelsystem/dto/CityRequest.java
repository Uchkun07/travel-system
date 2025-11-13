package io.github.uchkun07.travelsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 城市请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "城市请求")
public class CityRequest {

    @Schema(description = "城市ID(修改时必填)")
    private Integer cityId;

    @Schema(description = "城市名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String cityName;

    @Schema(description = "所属国家")
    private String country;

    @Schema(description = "城市图片URL")
    private String cityUrl;

    @Schema(description = "平均气温(℃)")
    private BigDecimal averageTemperature;

    @Schema(description = "景点数量")
    private Integer attractionCount;

    @Schema(description = "热度值")
    private Integer popularity;

    @Schema(description = "城市简介")
    private String description;

    @Schema(description = "排序序号")
    private Integer sortOrder;

    @Schema(description = "状态(1=启用,0=禁用)")
    private Integer status;
}
