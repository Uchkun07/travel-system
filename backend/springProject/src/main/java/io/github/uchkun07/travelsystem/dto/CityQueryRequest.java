package io.github.uchkun07.travelsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 城市查询请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "城市查询请求")
public class CityQueryRequest {

    @Schema(description = "页码", example = "1")
    private Long pageNum;

    @Schema(description = "每页大小", example = "10")
    private Long pageSize;

    @Schema(description = "城市名称(模糊查询)")
    private String cityName;

    @Schema(description = "所属国家(模糊查询)")
    private String country;

    @Schema(description = "状态(1=启用,0=禁用)")
    private Integer status;
}
