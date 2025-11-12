package io.github.uchkun07.travelsystem.controller;

import io.github.uchkun07.travelsystem.annotation.OperationLog;
import io.github.uchkun07.travelsystem.annotation.RequireAdminPermission;
import io.github.uchkun07.travelsystem.dto.*;
import io.github.uchkun07.travelsystem.entity.AttractionTag;
import io.github.uchkun07.travelsystem.service.IAttractionTagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 景点标签控制器
 */
@Slf4j
@Tag(name = "景点标签管理", description = "景点标签的增删改查接口")
@RestController
@RequestMapping("/admin/attraction-tag")
@RequireAdminPermission
public class AttractionTagController {

    @Autowired
    private IAttractionTagService tagService;

    @Operation(summary = "创建标签")
    @PostMapping("/create")
    @RequireAdminPermission(value = {"ATTRACTION_TAG:CREATE", "SYSTEM:MANAGE"})
    @OperationLog(type = "创建", object = "景点标签")
    public ApiResponse<AttractionTag> createTag(@Validated @RequestBody AttractionTagRequest request) {
        try {
            AttractionTag tag = tagService.createTag(request);
            return ApiResponse.success("创建成功", tag);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("创建标签失败", e);
            return ApiResponse.error(500, "创建失败");
        }
    }

    @Operation(summary = "删除标签")
    @DeleteMapping("/delete/{tagId}")
    @RequireAdminPermission(value = {"ATTRACTION_TAG:DELETE", "SYSTEM:MANAGE"})
    @OperationLog(type = "删除", object = "景点标签")
    public ApiResponse<Void> deleteTag(@PathVariable Integer tagId) {
        try {
            tagService.deleteTag(tagId);
            return ApiResponse.success("删除成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("删除标签失败", e);
            return ApiResponse.error(500, "删除失败");
        }
    }

    @Operation(summary = "批量删除标签")
    @DeleteMapping("/batch-delete")
    @RequireAdminPermission(value = {"ATTRACTION_TAG:DELETE", "SYSTEM:MANAGE"})
    @OperationLog(type = "批量删除", object = "景点标签")
    public ApiResponse<Void> batchDeleteTags(@RequestBody List<Integer> tagIds) {
        try {
            tagService.batchDeleteTags(tagIds);
            return ApiResponse.success("批量删除成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("批量删除标签失败", e);
            return ApiResponse.error(500, "批量删除失败");
        }
    }

    @Operation(summary = "修改标签")
    @PutMapping("/update")
    @RequireAdminPermission(value = {"ATTRACTION_TAG:UPDATE", "SYSTEM:MANAGE"})
    @OperationLog(type = "修改", object = "景点标签")
    public ApiResponse<AttractionTag> updateTag(@Validated @RequestBody AttractionTagRequest request) {
        try {
            AttractionTag tag = tagService.updateTag(request);
            return ApiResponse.success("修改成功", tag);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("修改标签失败", e);
            return ApiResponse.error(500, "修改失败");
        }
    }

    @Operation(summary = "分页查询标签")
    @GetMapping("/list")
    @RequireAdminPermission(value = {"ATTRACTION_TAG:VIEW", "SYSTEM:MANAGE"})
    public ApiResponse<PageResponse<AttractionTag>> queryTags(
            @RequestParam(defaultValue = "1") Long pageNum,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) String tagName,
            @RequestParam(required = false) Integer status) {
        try {
            AttractionTagQueryRequest request = AttractionTagQueryRequest.builder()
                    .pageNum(pageNum)
                    .pageSize(pageSize)
                    .tagName(tagName)
                    .status(status)
                    .build();
            PageResponse<AttractionTag> result = tagService.queryTags(request);
            return ApiResponse.success("查询成功", result);
        } catch (Exception e) {
            log.error("查询标签失败", e);
            return ApiResponse.error(500, "查询失败");
        }
    }

    @Operation(summary = "查询标签详情")
    @GetMapping("/detail/{tagId}")
    @RequireAdminPermission(value = {"ATTRACTION_TAG:VIEW", "SYSTEM:MANAGE"})
    public ApiResponse<AttractionTag> getTagById(@PathVariable Integer tagId) {
        try {
            AttractionTag tag = tagService.getTagById(tagId);
            return ApiResponse.success("查询成功", tag);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("查询标签详情失败", e);
            return ApiResponse.error(500, "查询失败");
        }
    }

    @Operation(summary = "获取所有标签")
    @GetMapping("/all")
    @RequireAdminPermission(value = {"ATTRACTION_TAG:VIEW", "SYSTEM:MANAGE"})
    public ApiResponse<List<AttractionTag>> getAllTags() {
        try {
            List<AttractionTag> tags = tagService.getAllTags();
            return ApiResponse.success("查询成功", tags);
        } catch (Exception e) {
            log.error("获取所有标签失败", e);
            return ApiResponse.error(500, "查询失败");
        }
    }
}
