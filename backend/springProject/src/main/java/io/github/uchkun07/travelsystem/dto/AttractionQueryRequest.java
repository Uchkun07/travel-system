package io.github.uchkun07.travelsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 景点分页查询请求DTO
 */
@Data
@Schema(description = "景点分页查询请求")
public class AttractionQueryRequest {

    @Schema(description = "当前页码", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer current = 1;

    @Schema(description = "每页大小", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer size = 10;

    @Schema(description = "景点名称（模糊查询）")
    private String name;

    @Schema(description = "景点类型ID")
    private Integer typeId;

    @Schema(description = "城市ID")
    private Integer cityId;

    @Schema(description = "景点状态（1=正常，0=下架）")
    private Integer status;

    @Schema(description = "审核状态（1=待审核，2=已通过，3=已驳回）")
    private Integer auditStatus;

    @Schema(description = "排序字段（popularity-人气，browse_count-浏览量，favorite_count-收藏数）")
    private String orderBy = "popularity";

    @Schema(description = "排序方式（asc-升序，desc-降序）")
    private String orderType = "desc";
}
