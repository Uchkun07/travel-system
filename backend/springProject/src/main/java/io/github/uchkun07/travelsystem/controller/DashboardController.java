package io.github.uchkun07.travelsystem.controller;

import io.github.uchkun07.travelsystem.annotation.RequireAdminPermission;
import io.github.uchkun07.travelsystem.dto.ApiResponse;
import io.github.uchkun07.travelsystem.dto.DashboardDTO;
import io.github.uchkun07.travelsystem.service.IDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Dashboard 数据大屏控制器
 */
@Slf4j
@Tag(name = "Dashboard 数据大屏", description = "管理端数据大屏聚合接口")
@RestController
@RequestMapping("/api/admin/dashboard")
@RequireAdminPermission
public class DashboardController {

    private final IDashboardService dashboardService;

    public DashboardController(IDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * 获取 Dashboard 全量数据
     * 包含：概览卡、30 天趋势、TOP10 景点、三个分布饼图、最近路线记录
     */
    @Operation(summary = "获取 Dashboard 大屏数据")
    @GetMapping
    public ApiResponse<DashboardDTO> getDashboard() {
        try {
            DashboardDTO data = dashboardService.getDashboardData();
            return ApiResponse.success("ok", data);
        } catch (Exception e) {
            log.error("获取 Dashboard 数据失败", e);
            return ApiResponse.error(500, "数据获取失败：" + e.getMessage());
        }
    }
}
