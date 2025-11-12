package io.github.uchkun07.travelsystem.controller;

import io.github.uchkun07.travelsystem.annotation.OperationLog;
import io.github.uchkun07.travelsystem.annotation.RequireAdminPermission;
import io.github.uchkun07.travelsystem.dto.*;
import io.github.uchkun07.travelsystem.entity.AttractionTag;
import io.github.uchkun07.travelsystem.service.IAttractionTagRelationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 景点-标签关联控制器
 */
@Slf4j
@Tag(name = "景点标签关联管理", description = "景点与标签的绑定和解绑接口")
@RestController
@RequestMapping("/admin/attraction-tag-relation")
@RequireAdminPermission
public class AttractionTagRelationController {

    @Autowired
    private IAttractionTagRelationService relationService;

    @Operation(summary = "景点绑定标签")
    @PostMapping("/bind")
    @RequireAdminPermission(value = {"ATTRACTION_TAG_RELATION:BIND", "SYSTEM:MANAGE"})
    @OperationLog(type = "绑定", object = "景点标签")
    public ApiResponse<Void> bindTag(@Validated @RequestBody AttractionTagBindRequest request) {
        try {
            relationService.bindTag(request);
            return ApiResponse.success("绑定成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("景点绑定标签失败", e);
            return ApiResponse.error(500, "绑定失败");
        }
    }

    @Operation(summary = "景点解绑标签")
    @PostMapping("/unbind")
    @RequireAdminPermission(value = {"ATTRACTION_TAG_RELATION:UNBIND", "SYSTEM:MANAGE"})
    @OperationLog(type = "解绑", object = "景点标签")
    public ApiResponse<Void> unbindTag(@Validated @RequestBody AttractionTagUnbindRequest request) {
        try {
            relationService.unbindTag(request);
            return ApiResponse.success("解绑成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("景点解绑标签失败", e);
            return ApiResponse.error(500, "解绑失败");
        }
    }

    @Operation(summary = "景点批量绑定标签")
    @PostMapping("/batch-bind")
    @RequireAdminPermission(value = {"ATTRACTION_TAG_RELATION:BIND", "SYSTEM:MANAGE"})
    @OperationLog(type = "批量绑定", object = "景点标签")
    public ApiResponse<Void> batchBindTags(@Validated @RequestBody AttractionTagBatchBindRequest request) {
        try {
            relationService.batchBindTags(request);
            return ApiResponse.success("批量绑定成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("景点批量绑定标签失败", e);
            return ApiResponse.error(500, "批量绑定失败");
        }
    }

    @Operation(summary = "景点批量解绑标签")
    @PostMapping("/batch-unbind")
    @RequireAdminPermission(value = {"ATTRACTION_TAG_RELATION:UNBIND", "SYSTEM:MANAGE"})
    @OperationLog(type = "批量解绑", object = "景点标签")
    public ApiResponse<Void> batchUnbindTags(@Validated @RequestBody AttractionTagBatchBindRequest request) {
        try {
            relationService.batchUnbindTags(request);
            return ApiResponse.success("批量解绑成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("景点批量解绑标签失败", e);
            return ApiResponse.error(500, "批量解绑失败");
        }
    }

    @Operation(summary = "查询景点的所有标签")
    @GetMapping("/list/{attractionId}")
    @RequireAdminPermission(value = {"ATTRACTION_TAG_RELATION:VIEW", "SYSTEM:MANAGE"})
    public ApiResponse<List<AttractionTag>> getAttractionTags(@PathVariable Long attractionId) {
        try {
            List<AttractionTag> tags = relationService.getAttractionTags(attractionId);
            return ApiResponse.success("查询成功", tags);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("查询景点标签失败", e);
            return ApiResponse.error(500, "查询失败");
        }
    }
}
