package io.github.uchkun07.travelsystem.controller;

import io.github.uchkun07.travelsystem.dto.ApiResponse;
import io.github.uchkun07.travelsystem.dto.CityQueryRequest;
import io.github.uchkun07.travelsystem.dto.CityResponse;
import io.github.uchkun07.travelsystem.dto.PageResponse;
import io.github.uchkun07.travelsystem.service.ICityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 城市C端控制器
 */
@Slf4j
@Tag(name = "城市(C端)", description = "城市前台展示接口")
@RestController
@RequestMapping("/api/city")
public class CityHomeController {

    @Autowired
    private ICityService cityService;

    @Operation(summary = "分页获取城市列表", description = "前台城市列表展示，支持分页和筛选")
    @PostMapping("/list")
    public ApiResponse<PageResponse<CityResponse>> getCityList( @RequestBody CityQueryRequest request) {
        try {
            PageResponse<CityResponse> page = cityService.getCityCards(request);
            return ApiResponse.success("获取成功", page);
        } catch (Exception e) {
            log.error("获取城市列表失败", e);
            return ApiResponse.error(500, "获取失败: " + e.getMessage());
        }
    }
}