package io.github.uchkun07.travelsystem.controller;

import io.github.uchkun07.travelsystem.annotation.RequireAdminPermission;
import io.github.uchkun07.travelsystem.dto.ApiResponse;
import io.github.uchkun07.travelsystem.dto.OperationLogQueryRequest;
import io.github.uchkun07.travelsystem.dto.PageResponse;
import io.github.uchkun07.travelsystem.entity.OperationLog;
import io.github.uchkun07.travelsystem.service.IOperationLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Tag(name = "操作日志管理", description = "操作日志的查询和删除接口")
@RestController
@RequestMapping("/admin/operation-log")
@RequireAdminPermission
public class OperationLogController {

    @Autowired
    private IOperationLogService operationLogService;

    @Operation(summary = "删除操作日志")
    @DeleteMapping("/delete/{operationLogId}")
    @RequireAdminPermission(value = {"OPERATION_LOG:DELETE", "SYSTEM:MANAGE"})
    @io.github.uchkun07.travelsystem.annotation.OperationLog(type = "删除", object = "操作日志")
    public ApiResponse<Void> deleteLog(@PathVariable Long operationLogId) {
        try {
            operationLogService.deleteLog(operationLogId);
            return ApiResponse.success("删除成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("删除操作日志失败", e);
            return ApiResponse.error(500, "删除失败");
        }
    }

    @Operation(summary = "批量删除操作日志")
    @DeleteMapping("/batch-delete")
    @RequireAdminPermission(value = {"OPERATION_LOG:DELETE", "SYSTEM:MANAGE"})
    @io.github.uchkun07.travelsystem.annotation.OperationLog(type = "批量删除", object = "操作日志")
    public ApiResponse<Void> batchDeleteLogs(@RequestBody List<Long> operationLogIds) {
        try {
            operationLogService.batchDeleteLogs(operationLogIds);
            return ApiResponse.success("批量删除成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("批量删除操作日志失败", e);
            return ApiResponse.error(500, "批量删除失败");
        }
    }

    @Operation(summary = "分页查询操作日志")
    @GetMapping("/list")
    @RequireAdminPermission(value = {"OPERATION_LOG:VIEW", "SYSTEM:MANAGE"})
    public ApiResponse<PageResponse<OperationLog>> queryLogs(
            @RequestParam(defaultValue = "1") Long pageNum,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) Long adminId,
            @RequestParam(required = false) String operationType,
            @RequestParam(required = false) String operationObject,
            @RequestParam(required = false) Long objectId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        try {
            OperationLogQueryRequest request = OperationLogQueryRequest.builder()
                    .pageNum(pageNum)
                    .pageSize(pageSize)
                    .adminId(adminId)
                    .operationType(operationType)
                    .operationObject(operationObject)
                    .objectId(objectId)
                    .startTime(startTime)
                    .endTime(endTime)
                    .build();
            
            PageResponse<OperationLog> result = operationLogService.queryLogs(request);
            return ApiResponse.success("查询成功", result);
        } catch (Exception e) {
            log.error("查询操作日志失败", e);
            return ApiResponse.error(500, "查询失败");
        }
    }
}
