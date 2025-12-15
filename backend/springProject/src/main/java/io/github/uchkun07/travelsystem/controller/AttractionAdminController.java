package io.github.uchkun07.travelsystem.controller;

import io.github.uchkun07.travelsystem.annotation.OperationLog;
import io.github.uchkun07.travelsystem.annotation.RequireAdminPermission;
import io.github.uchkun07.travelsystem.dto.*;
import io.github.uchkun07.travelsystem.entity.*;
import io.github.uchkun07.travelsystem.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 景点综合管理控制器
 * 整合景点、景点类型、城市、景点标签、景点标签关联等相关功能
 */
@Slf4j
@Tag(name = "景点管理", description = "景点及相关资源的统一管理接口")
@RestController
@RequestMapping("/api/admin/attraction")
@RequireAdminPermission
public class AttractionAdminController {

    @Autowired
    private IAttractionService attractionService;

    @Autowired
    private IAttractionTypeService attractionTypeService;

    @Autowired
    private ICityService cityService;

    @Autowired
    private IAttractionTagService attractionTagService;

    @Autowired
    private IAttractionTagRelationService attractionTagRelationService;

    // ==================== 景点管理 ====================

    @Operation(summary = "创建景点")
    @PostMapping("/create")
    @RequireAdminPermission(value = { "ATTRACTION:CREATE", "SYSTEM:MANAGE" })
    @OperationLog(type = "创建", object = "景点")
    public ApiResponse<Long> createAttraction(@Validated @RequestBody AttractionRequest request) {
        try {
            Long attractionId = attractionService.createAttraction(request);
            return ApiResponse.success("创建成功", attractionId);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("创建景点失败", e);
            return ApiResponse.error(500, "创建失败: " + e.getMessage());
        }
    }

    @Operation(summary = "删除景点")
    @DeleteMapping("/delete/{attractionId}")
    @RequireAdminPermission(value = { "ATTRACTION:DELETE", "SYSTEM:MANAGE" })
    @OperationLog(type = "删除", object = "景点")
    public ApiResponse<Void> deleteAttraction(@PathVariable Long attractionId) {
        try {
            attractionService.deleteAttraction(attractionId);
            return ApiResponse.success("删除成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("删除景点失败", e);
            return ApiResponse.error(500, "删除失败: " + e.getMessage());
        }
    }

    @Operation(summary = "更新景点")
    @PutMapping("/update")
    @RequireAdminPermission(value = { "ATTRACTION:UPDATE", "SYSTEM:MANAGE" })
    @OperationLog(type = "更新", object = "景点")
    public ApiResponse<Void> updateAttraction(@Validated @RequestBody AttractionRequest request) {
        try {
            attractionService.updateAttraction(request);
            return ApiResponse.success("更新成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("更新景点失败", e);
            return ApiResponse.error(500, "更新失败: " + e.getMessage());
        }
    }

    @Operation(summary = "分页查询景点列表", description = "获取景点列表(景点ID、名称、类型、城市、浏览量、收藏数、人气指数)")
    @PostMapping("/list")
    @RequireAdminPermission(value = { "ATTRACTION:LIST", "SYSTEM:MANAGE" })
    public ApiResponse<PageResponse<AttractionListResponse>> listAttractions(
            @Validated @RequestBody AttractionQueryRequest request) {
        try {
            PageResponse<AttractionListResponse> page = attractionService.listAttractions(request);
            return ApiResponse.success("查询成功", page);
        } catch (Exception e) {
            log.error("查询景点列表失败", e);
            return ApiResponse.error(500, "查询失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取景点详情", description = "获取景点的完整信息,包括绑定的标签")
    @GetMapping("/detail/{attractionId}")
    @RequireAdminPermission(value = { "ATTRACTION:DETAIL", "SYSTEM:MANAGE" })
    public ApiResponse<AttractionDetailResponse> getAttractionDetail(@PathVariable Long attractionId) {
        try {
            AttractionDetailResponse detail = attractionService.getAttractionDetail(attractionId);
            return ApiResponse.success("查询成功", detail);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("获取景点详情失败", e);
            return ApiResponse.error(500, "查询失败: " + e.getMessage());
        }
    }

    // ==================== 景点类型管理 ====================

    @Operation(summary = "创建景点类型")
    @PostMapping("/type/create")
    @RequireAdminPermission(value = { "ATTRACTION_TYPE:CREATE", "SYSTEM:MANAGE" })
    @OperationLog(type = "创建", object = "景点类型")
    public ApiResponse<AttractionType> createAttractionType(
            @Validated @RequestBody AttractionTypeCreateRequest request) {
        try {
            AttractionType attractionType = attractionTypeService.createAttractionType(request);
            log.info("创建景点类型成功: typeId={}, typeName={}", attractionType.getTypeId(), attractionType.getTypeName());
            return ApiResponse.success("创建成功", attractionType);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("创建景点类型失败", e);
            return ApiResponse.error(500, "创建失败");
        }
    }

    @Operation(summary = "删除景点类型")
    @DeleteMapping("/type/delete/{typeId}")
    @RequireAdminPermission(value = { "ATTRACTION_TYPE:DELETE", "SYSTEM:MANAGE" })
    @OperationLog(type = "删除", object = "景点类型")
    public ApiResponse<Void> deleteAttractionType(@PathVariable Integer typeId) {
        try {
            attractionTypeService.deleteAttractionType(typeId);
            return ApiResponse.success("删除成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("删除景点类型失败", e);
            return ApiResponse.error(500, "删除失败");
        }
    }

    @Operation(summary = "批量删除景点类型")
    @DeleteMapping("/type/batch-delete")
    @RequireAdminPermission(value = { "ATTRACTION_TYPE:DELETE", "SYSTEM:MANAGE" })
    @OperationLog(type = "批量删除", object = "景点类型")
    public ApiResponse<Void> batchDeleteAttractionTypes(@RequestBody List<Integer> typeIds) {
        try {
            attractionTypeService.batchDeleteAttractionTypes(typeIds);
            return ApiResponse.success("批量删除成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("批量删除景点类型失败", e);
            return ApiResponse.error(500, "批量删除失败");
        }
    }

    @Operation(summary = "修改景点类型")
    @PutMapping("/type/update")
    @RequireAdminPermission(value = { "ATTRACTION_TYPE:UPDATE", "SYSTEM:MANAGE" })
    @OperationLog(type = "修改", object = "景点类型")
    public ApiResponse<AttractionType> updateAttractionType(
            @Validated @RequestBody AttractionTypeUpdateRequest request) {
        try {
            AttractionType attractionType = attractionTypeService.updateAttractionType(request);
            return ApiResponse.success("修改成功", attractionType);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("修改景点类型失败", e);
            return ApiResponse.error(500, "修改失败");
        }
    }

    @Operation(summary = "分页查询景点类型")
    @GetMapping("/type/list")
    @RequireAdminPermission(value = { "ATTRACTION_TYPE:VIEW", "SYSTEM:MANAGE" })
    public ApiResponse<PageResponse<AttractionType>> queryAttractionTypes(
            @RequestParam(defaultValue = "1") Long pageNum,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) Integer typeId,
            @RequestParam(required = false) String typeName,
            @RequestParam(required = false) Integer status) {
        try {
            AttractionTypeQueryRequest request = AttractionTypeQueryRequest.builder()
                    .pageNum(pageNum).pageSize(pageSize).typeId(typeId).typeName(typeName).status(status).build();
            PageResponse<AttractionType> result = attractionTypeService.queryAttractionTypes(request);
            return ApiResponse.success("查询成功", result);
        } catch (Exception e) {
            log.error("查询景点类型失败", e);
            return ApiResponse.error(500, "查询失败");
        }
    }

    @Operation(summary = "查询景点类型详情")
    @GetMapping("/type/detail/{typeId}")
    @RequireAdminPermission(value = { "ATTRACTION_TYPE:VIEW", "SYSTEM:MANAGE" })
    public ApiResponse<AttractionType> getAttractionTypeById(@PathVariable Integer typeId) {
        try {
            AttractionType attractionType = attractionTypeService.getAttractionTypeById(typeId);
            return ApiResponse.success("查询成功", attractionType);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("查询景点类型详情失败", e);
            return ApiResponse.error(500, "查询失败");
        }
    }

    // ==================== 城市管理 ====================

    @Operation(summary = "创建城市")
    @PostMapping("/city/create")
    @RequireAdminPermission(value = { "CITY:CREATE", "SYSTEM:MANAGE" })
    @OperationLog(type = "创建", object = "城市")
    public ApiResponse<City> createCity(@Validated @RequestBody CityRequest request) {
        try {
            City city = cityService.createCity(request);
            return ApiResponse.success("创建成功", city);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("创建城市失败", e);
            return ApiResponse.error(500, "创建失败");
        }
    }

    @Operation(summary = "删除城市")
    @DeleteMapping("/city/delete/{cityId}")
    @RequireAdminPermission(value = { "CITY:DELETE", "SYSTEM:MANAGE" })
    @OperationLog(type = "删除", object = "城市")
    public ApiResponse<Void> deleteCity(@PathVariable Integer cityId) {
        try {
            cityService.deleteCity(cityId);
            return ApiResponse.success("删除成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("删除城市失败", e);
            return ApiResponse.error(500, "删除失败");
        }
    }

    @Operation(summary = "批量删除城市")
    @DeleteMapping("/city/batch-delete")
    @RequireAdminPermission(value = { "CITY:DELETE", "SYSTEM:MANAGE" })
    @OperationLog(type = "批量删除", object = "城市")
    public ApiResponse<Void> batchDeleteCities(@RequestBody List<Integer> cityIds) {
        try {
            cityService.batchDeleteCities(cityIds);
            return ApiResponse.success("批量删除成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("批量删除城市失败", e);
            return ApiResponse.error(500, "批量删除失败");
        }
    }

    @Operation(summary = "修改城市")
    @PutMapping("/city/update")
    @RequireAdminPermission(value = { "CITY:UPDATE", "SYSTEM:MANAGE" })
    @OperationLog(type = "修改", object = "城市")
    public ApiResponse<City> updateCity(@Validated @RequestBody CityRequest request) {
        try {
            City city = cityService.updateCity(request);
            return ApiResponse.success("修改成功", city);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("修改城市失败", e);
            return ApiResponse.error(500, "修改失败");
        }
    }

    @Operation(summary = "分页查询城市")
    @GetMapping("/city/list")
    @RequireAdminPermission(value = { "CITY:VIEW", "SYSTEM:MANAGE" })
    public ApiResponse<PageResponse<City>> queryCities(
            @RequestParam(defaultValue = "1") Long pageNum,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) String cityName,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) Integer status) {
        try {
            CityQueryRequest request = CityQueryRequest.builder()
                    .pageNum(pageNum).pageSize(pageSize).cityName(cityName).country(country)
                    .status(status).build();
            PageResponse<City> result = cityService.queryCities(request);
            return ApiResponse.success("查询成功", result);
        } catch (Exception e) {
            log.error("查询城市失败", e);
            return ApiResponse.error(500, "查询失败");
        }
    }

    @Operation(summary = "查询城市详情")
    @GetMapping("/city/detail/{cityId}")
    @RequireAdminPermission(value = { "CITY:VIEW", "SYSTEM:MANAGE" })
    public ApiResponse<City> getCityById(@PathVariable Integer cityId) {
        try {
            City city = cityService.getCityById(cityId);
            return ApiResponse.success("查询成功", city);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("查询城市详情失败", e);
            return ApiResponse.error(500, "查询失败");
        }
    }

    @Operation(summary = "获取所有城市")
    @GetMapping("/city/all")
    @RequireAdminPermission(value = { "CITY:VIEW", "SYSTEM:MANAGE" })
    public ApiResponse<List<City>> getAllCities() {
        try {
            List<City> cities = cityService.getAllCities();
            return ApiResponse.success("查询成功", cities);
        } catch (Exception e) {
            log.error("获取所有城市失败", e);
            return ApiResponse.error(500, "查询失败");
        }
    }

    // ==================== 景点标签管理 ====================

    @Operation(summary = "创建景点标签")
    @PostMapping("/tag/create")
    @RequireAdminPermission(value = { "ATTRACTION_TAG:CREATE", "SYSTEM:MANAGE" })
    @OperationLog(type = "创建", object = "景点标签")
    public ApiResponse<AttractionTag> createTag(@Validated @RequestBody AttractionTagRequest request) {
        try {
            AttractionTag tag = attractionTagService.createTag(request);
            return ApiResponse.success("创建成功", tag);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("创建标签失败", e);
            return ApiResponse.error(500, "创建失败");
        }
    }

    @Operation(summary = "删除景点标签")
    @DeleteMapping("/tag/delete/{tagId}")
    @RequireAdminPermission(value = { "ATTRACTION_TAG:DELETE", "SYSTEM:MANAGE" })
    @OperationLog(type = "删除", object = "景点标签")
    public ApiResponse<Void> deleteTag(@PathVariable Integer tagId) {
        try {
            attractionTagService.deleteTag(tagId);
            return ApiResponse.success("删除成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("删除标签失败", e);
            return ApiResponse.error(500, "删除失败");
        }
    }

    @Operation(summary = "批量删除景点标签")
    @DeleteMapping("/tag/batch-delete")
    @RequireAdminPermission(value = { "ATTRACTION_TAG:DELETE", "SYSTEM:MANAGE" })
    @OperationLog(type = "批量删除", object = "景点标签")
    public ApiResponse<Void> batchDeleteTags(@RequestBody List<Integer> tagIds) {
        try {
            attractionTagService.batchDeleteTags(tagIds);
            return ApiResponse.success("批量删除成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("批量删除标签失败", e);
            return ApiResponse.error(500, "批量删除失败");
        }
    }

    @Operation(summary = "修改景点标签")
    @PutMapping("/tag/update")
    @RequireAdminPermission(value = { "ATTRACTION_TAG:UPDATE", "SYSTEM:MANAGE" })
    @OperationLog(type = "修改", object = "景点标签")
    public ApiResponse<AttractionTag> updateTag(@Validated @RequestBody AttractionTagRequest request) {
        try {
            AttractionTag tag = attractionTagService.updateTag(request);
            return ApiResponse.success("修改成功", tag);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("修改标签失败", e);
            return ApiResponse.error(500, "修改失败");
        }
    }

    @Operation(summary = "分页查询景点标签")
    @GetMapping("/tag/list")
    @RequireAdminPermission(value = { "ATTRACTION_TAG:VIEW", "SYSTEM:MANAGE" })
    public ApiResponse<PageResponse<AttractionTag>> queryTags(
            @RequestParam(defaultValue = "1") Long pageNum,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) String tagName,
            @RequestParam(required = false) Integer status) {
        try {
            AttractionTagQueryRequest request = AttractionTagQueryRequest.builder()
                    .pageNum(pageNum).pageSize(pageSize).tagName(tagName).status(status).build();
            PageResponse<AttractionTag> result = attractionTagService.queryTags(request);
            return ApiResponse.success("查询成功", result);
        } catch (Exception e) {
            log.error("查询标签失败", e);
            return ApiResponse.error(500, "查询失败");
        }
    }

    @Operation(summary = "查询景点标签详情")
    @GetMapping("/tag/detail/{tagId}")
    @RequireAdminPermission(value = { "ATTRACTION_TAG:VIEW", "SYSTEM:MANAGE" })
    public ApiResponse<AttractionTag> getTagById(@PathVariable Integer tagId) {
        try {
            AttractionTag tag = attractionTagService.getTagById(tagId);
            return ApiResponse.success("查询成功", tag);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("查询标签详情失败", e);
            return ApiResponse.error(500, "查询失败");
        }
    }

    @Operation(summary = "获取所有景点标签")
    @GetMapping("/tag/all")
    @RequireAdminPermission(value = { "ATTRACTION_TAG:VIEW", "SYSTEM:MANAGE" })
    public ApiResponse<List<AttractionTag>> getAllTags() {
        try {
            List<AttractionTag> tags = attractionTagService.getAllTags();
            return ApiResponse.success("查询成功", tags);
        } catch (Exception e) {
            log.error("获取所有标签失败", e);
            return ApiResponse.error(500, "查询失败");
        }
    }

    // ==================== 景点标签关联管理 ====================

    @Operation(summary = "景点绑定标签")
    @PostMapping("/tag-relation/bind")
    @RequireAdminPermission(value = { "ATTRACTION_TAG_RELATION:BIND", "SYSTEM:MANAGE" })
    @OperationLog(type = "绑定", object = "景点标签")
    public ApiResponse<Void> bindTag(@Validated @RequestBody AttractionTagBindRequest request) {
        try {
            attractionTagRelationService.bindTag(request);
            return ApiResponse.success("绑定成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("景点绑定标签失败", e);
            return ApiResponse.error(500, "绑定失败");
        }
    }

    @Operation(summary = "景点解绑标签")
    @PostMapping("/tag-relation/unbind")
    @RequireAdminPermission(value = { "ATTRACTION_TAG_RELATION:UNBIND", "SYSTEM:MANAGE" })
    @OperationLog(type = "解绑", object = "景点标签")
    public ApiResponse<Void> unbindTag(@Validated @RequestBody AttractionTagUnbindRequest request) {
        try {
            attractionTagRelationService.unbindTag(request);
            return ApiResponse.success("解绑成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("景点解绑标签失败", e);
            return ApiResponse.error(500, "解绑失败");
        }
    }

    @Operation(summary = "景点批量绑定标签")
    @PostMapping("/tag-relation/batch-bind")
    @RequireAdminPermission(value = { "ATTRACTION_TAG_RELATION:BIND", "SYSTEM:MANAGE" })
    @OperationLog(type = "批量绑定", object = "景点标签")
    public ApiResponse<Void> batchBindTags(@Validated @RequestBody AttractionTagBatchBindRequest request) {
        try {
            attractionTagRelationService.batchBindTags(request);
            return ApiResponse.success("批量绑定成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("景点批量绑定标签失败", e);
            return ApiResponse.error(500, "批量绑定失败");
        }
    }

    @Operation(summary = "景点批量解绑标签")
    @PostMapping("/tag-relation/batch-unbind")
    @RequireAdminPermission(value = { "ATTRACTION_TAG_RELATION:UNBIND", "SYSTEM:MANAGE" })
    @OperationLog(type = "批量解绑", object = "景点标签")
    public ApiResponse<Void> batchUnbindTags(@Validated @RequestBody AttractionTagBatchBindRequest request) {
        try {
            attractionTagRelationService.batchUnbindTags(request);
            return ApiResponse.success("批量解绑成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("景点批量解绑标签失败", e);
            return ApiResponse.error(500, "批量解绑失败");
        }
    }

    @Operation(summary = "查询景点的所有标签")
    @GetMapping("/tag-relation/list/{attractionId}")
    @RequireAdminPermission(value = { "ATTRACTION_TAG_RELATION:VIEW", "SYSTEM:MANAGE" })
    public ApiResponse<List<AttractionTag>> getAttractionTags(@PathVariable Long attractionId) {
        try {
            List<AttractionTag> tags = attractionTagRelationService.getAttractionTags(attractionId);
            return ApiResponse.success("查询成功", tags);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("查询景点标签失败", e);
            return ApiResponse.error(500, "查询失败");
        }
    }
}
