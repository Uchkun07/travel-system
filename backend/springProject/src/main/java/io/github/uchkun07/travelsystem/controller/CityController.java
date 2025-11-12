package io.github.uchkun07.travelsystem.controller;

import io.github.uchkun07.travelsystem.annotation.OperationLog;
import io.github.uchkun07.travelsystem.annotation.RequireAdminPermission;
import io.github.uchkun07.travelsystem.dto.*;
import io.github.uchkun07.travelsystem.entity.City;
import io.github.uchkun07.travelsystem.service.ICityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 城市控制器
 */
@Slf4j
@Tag(name = "城市管理", description = "城市的增删改查接口")
@RestController
@RequestMapping("/admin/city")
@RequireAdminPermission
public class CityController {

    @Autowired
    private ICityService cityService;

    @Operation(summary = "创建城市")
    @PostMapping("/create")
    @RequireAdminPermission(value = {"CITY:CREATE", "SYSTEM:MANAGE"})
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
    @DeleteMapping("/delete/{cityId}")
    @RequireAdminPermission(value = {"CITY:DELETE", "SYSTEM:MANAGE"})
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
    @DeleteMapping("/batch-delete")
    @RequireAdminPermission(value = {"CITY:DELETE", "SYSTEM:MANAGE"})
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
    @PutMapping("/update")
    @RequireAdminPermission(value = {"CITY:UPDATE", "SYSTEM:MANAGE"})
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
    @GetMapping("/list")
    @RequireAdminPermission(value = {"CITY:VIEW", "SYSTEM:MANAGE"})
    public ApiResponse<PageResponse<City>> queryCities(
            @RequestParam(defaultValue = "1") Long pageNum,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) String cityName,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer minPopularity,
            @RequestParam(required = false) Integer maxPopularity) {
        try {
            CityQueryRequest request = CityQueryRequest.builder()
                    .pageNum(pageNum)
                    .pageSize(pageSize)
                    .cityName(cityName)
                    .country(country)
                    .status(status)
                    .minPopularity(minPopularity)
                    .maxPopularity(maxPopularity)
                    .build();
            PageResponse<City> result = cityService.queryCities(request);
            return ApiResponse.success("查询成功", result);
        } catch (Exception e) {
            log.error("查询城市失败", e);
            return ApiResponse.error(500, "查询失败");
        }
    }

    @Operation(summary = "查询城市详情")
    @GetMapping("/detail/{cityId}")
    @RequireAdminPermission(value = {"CITY:VIEW", "SYSTEM:MANAGE"})
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
    @GetMapping("/all")
    @RequireAdminPermission(value = {"CITY:VIEW", "SYSTEM:MANAGE"})
    public ApiResponse<List<City>> getAllCities() {
        try {
            List<City> cities = cityService.getAllCities();
            return ApiResponse.success("查询成功", cities);
        } catch (Exception e) {
            log.error("获取所有城市失败", e);
            return ApiResponse.error(500, "查询失败");
        }
    }
}
