package io.github.uchkun07.travelsystem.controller;

import io.github.uchkun07.travelsystem.annotation.RequireAdminPermission;
import io.github.uchkun07.travelsystem.dto.*;
import io.github.uchkun07.travelsystem.entity.AttractionType;
import io.github.uchkun07.travelsystem.service.IAttractionTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 景点类型控制器
 */
@Slf4j
@Tag(name = "景点类型管理", description = "景点类型的增删改查接口")
@RestController
@RequestMapping("/api/admin/attraction-type")
@RequireAdminPermission // 类级别注解，所有接口都需要管理员权限
public class AttractionTypeController {

    @Autowired
    private IAttractionTypeService attractionTypeService;

    /**
     * 创建景点类型
     */
    @Operation(summary = "创建景点类型", description = "创建一个新的景点类型")
    @PostMapping("/create")
    @RequireAdminPermission(value = {"ATTRACTION_TYPE:CREATE", "ATTRACTION:MANAGE"})
    @io.github.uchkun07.travelsystem.annotation.OperationLog(type = "创建", object = "景点类型")
    public ApiResponse<AttractionType> createAttractionType(
            @Validated @RequestBody AttractionTypeCreateRequest request) {
        try {
            AttractionType attractionType = attractionTypeService.createAttractionType(request);
            log.info("创建景点类型成功: typeId={}, typeName={}", 
                    attractionType.getTypeId(), attractionType.getTypeName());
            return ApiResponse.success("创建成功", attractionType);
        } catch (IllegalArgumentException e) {
            log.warn("创建景点类型失败: {}", e.getMessage());
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("创建景点类型失败", e);
            return ApiResponse.error(500, "创建失败，请稍后重试");
        }
    }

    /**
     * 删除景点类型
     */
    @Operation(summary = "删除景点类型", description = "根据类型ID删除景点类型")
    @DeleteMapping("/delete/{typeId}")
    @RequireAdminPermission(value = {"ATTRACTION_TYPE:DELETE", "ATTRACTION:MANAGE"})
    @io.github.uchkun07.travelsystem.annotation.OperationLog(type = "删除", object = "景点类型")
    public ApiResponse<Void> deleteAttractionType(
            @Parameter(description = "类型ID") @PathVariable Integer typeId) {
        try {
            attractionTypeService.deleteAttractionType(typeId);
            log.info("删除景点类型成功: typeId={}", typeId);
            return ApiResponse.success("删除成功", null);
        } catch (IllegalArgumentException e) {
            log.warn("删除景点类型失败: {}", e.getMessage());
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("删除景点类型失败: typeId={}", typeId, e);
            return ApiResponse.error(500, "删除失败，请稍后重试");
        }
    }

    /**
     * 批量删除景点类型
     */
    @Operation(summary = "批量删除景点类型", description = "根据类型ID列表批量删除景点类型")
    @DeleteMapping("/batch-delete")
    @RequireAdminPermission(value = {"ATTRACTION_TYPE:DELETE", "ATTRACTION:MANAGE"})
    @io.github.uchkun07.travelsystem.annotation.OperationLog(type = "批量删除", object = "景点类型")
    public ApiResponse<Void> batchDeleteAttractionTypes(
            @Parameter(description = "类型ID列表") @RequestBody List<Integer> typeIds) {
        try {
            attractionTypeService.batchDeleteAttractionTypes(typeIds);
            log.info("批量删除景点类型成功: 删除数量={}", typeIds.size());
            return ApiResponse.success("批量删除成功", null);
        } catch (IllegalArgumentException e) {
            log.warn("批量删除景点类型失败: {}", e.getMessage());
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("批量删除景点类型失败", e);
            return ApiResponse.error(500, "批量删除失败，请稍后重试");
        }
    }

    /**
     * 修改景点类型
     */
    @Operation(summary = "修改景点类型", description = "修改景点类型的信息")
    @PutMapping("/update")
    @RequireAdminPermission(value = {"ATTRACTION_TYPE:UPDATE", "ATTRACTION:MANAGE"})
    @io.github.uchkun07.travelsystem.annotation.OperationLog(type = "修改", object = "景点类型")
    public ApiResponse<AttractionType> updateAttractionType(
            @Validated @RequestBody AttractionTypeUpdateRequest request) {
        try {
            AttractionType attractionType = attractionTypeService.updateAttractionType(request);
            log.info("修改景点类型成功: typeId={}, typeName={}", 
                    attractionType.getTypeId(), attractionType.getTypeName());
            return ApiResponse.success("修改成功", attractionType);
        } catch (IllegalArgumentException e) {
            log.warn("修改景点类型失败: {}", e.getMessage());
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("修改景点类型失败", e);
            return ApiResponse.error(500, "修改失败，请稍后重试");
        }
    }

    /**
     * 分页查询景点类型
     */
    @Operation(summary = "分页查询景点类型", 
               description = "分页查询景点类型列表，支持根据类型ID、类型名称、状态进行筛选")
    @GetMapping("/list")
    @RequireAdminPermission(value = {"ATTRACTION_TYPE:VIEW", "ATTRACTION:MANAGE"})
    public ApiResponse<PageResponse<AttractionType>> queryAttractionTypes(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Long pageNum,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Long pageSize,
            @Parameter(description = "类型ID（可选）") @RequestParam(required = false) Integer typeId,
            @Parameter(description = "类型名称（可选，模糊查询）") @RequestParam(required = false) String typeName,
            @Parameter(description = "状态（可选，1=启用，0=禁用）") @RequestParam(required = false) Integer status) {
        try {
            AttractionTypeQueryRequest request = AttractionTypeQueryRequest.builder()
                    .pageNum(pageNum)
                    .pageSize(pageSize)
                    .typeId(typeId)
                    .typeName(typeName)
                    .status(status)
                    .build();
            
            PageResponse<AttractionType> result = attractionTypeService.queryAttractionTypes(request);
            log.info("查询景点类型成功: pageNum={}, pageSize={}, total={}", 
                    pageNum, pageSize, result.getTotal());
            return ApiResponse.success("查询成功", result);
        } catch (Exception e) {
            log.error("查询景点类型失败", e);
            return ApiResponse.error(500, "查询失败，请稍后重试");
        }
    }

    /**
     * 根据ID查询景点类型详情
     */
    @Operation(summary = "查询景点类型详情", description = "根据类型ID查询景点类型详细信息")
    @GetMapping("/detail/{typeId}")
    @RequireAdminPermission(value = {"ATTRACTION_TYPE:VIEW", "ATTRACTION:MANAGE"})
    public ApiResponse<AttractionType> getAttractionTypeById(
            @Parameter(description = "类型ID") @PathVariable Integer typeId) {
        try {
            AttractionType attractionType = attractionTypeService.getAttractionTypeById(typeId);
            log.info("查询景点类型详情成功: typeId={}", typeId);
            return ApiResponse.success("查询成功", attractionType);
        } catch (IllegalArgumentException e) {
            log.warn("查询景点类型详情失败: {}", e.getMessage());
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("查询景点类型详情失败: typeId={}", typeId, e);
            return ApiResponse.error(500, "查询失败，请稍后重试");
        }
    }
}
