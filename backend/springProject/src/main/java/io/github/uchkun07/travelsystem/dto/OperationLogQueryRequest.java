package io.github.uchkun07.travelsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "操作日志查询请求")
public class OperationLogQueryRequest {

    @Builder.Default
    @Schema(description = "当前页码", example = "1")
    private Long pageNum = 1L;

    @Builder.Default
    @Schema(description = "每页条数", example = "10")
    private Long pageSize = 10L;

    @Schema(description = "操作人ID", example = "1")
    private Long adminId;

    @Schema(description = "操作类型", example = "创建")
    private String operationType;

    @Schema(description = "操作对象", example = "景点类型")
    private String operationObject;

    @Schema(description = "操作对象ID", example = "1")
    private Long objectId;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;
}
