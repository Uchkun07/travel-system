package io.github.uchkun07.travelsystem.service;

import io.github.uchkun07.travelsystem.dto.RoutePlanRequest;
import io.github.uchkun07.travelsystem.dto.RoutePlanResult;

/**
 * 路线规划服务接口
 */
public interface IRoutePlanService {

    /**
     * 执行路线规划
     *
     * @param request 前端请求参数
     * @return 规划结果（含各景点顺序、费用、耗时等）
     */
    RoutePlanResult plan(RoutePlanRequest request);
}
