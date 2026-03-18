package io.github.uchkun07.travelsystem.service;

import io.github.uchkun07.travelsystem.dto.DashboardDTO;

/**
 * Dashboard 数据大屏 Service 接口
 */
public interface IDashboardService {

    /**
     * 获取 Dashboard 全量数据（概览卡 + 趋势 + 图表 + 最近记录）
     */
    DashboardDTO getDashboardData();
}
