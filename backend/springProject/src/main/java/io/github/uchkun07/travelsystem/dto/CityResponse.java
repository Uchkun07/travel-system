package io.github.uchkun07.travelsystem.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "客户端城市响应")
public class CityResponse {

    @Schema(description = "城市名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String cityName;

    @Schema(description = "所属国家", requiredMode = Schema.RequiredMode.REQUIRED)
    private String country;

    @Schema(description = "城市简介")
    private String description;

    @Schema(description = "城市图片URL")
    private String cityUrl;

    @Schema(description = "平均气温（单位：℃）")
    private BigDecimal averageTemperature;

    @Schema(description = "城市内景点数量")
    private Integer attractionCount;

    @Schema(description = "热度值（热门城市排序依据）")
    private Integer popularity;
}
