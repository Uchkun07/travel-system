package io.github.uchkun07.travelsystem.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.uchkun07.travelsystem.dto.ApiResponse;
import io.github.uchkun07.travelsystem.entity.UserCountTable;
import io.github.uchkun07.travelsystem.mapper.UserCountTableMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户统计数据控制器
 */
@Slf4j
@Tag(name = "用户统计", description = "用户行为统计数据接口")
@RestController
@RequestMapping("/api/user/stats")
public class UserStatsController {

    @Autowired
    private UserCountTableMapper userCountTableMapper;

    /**
     * 获取用户统计数据
     */
    @Operation(summary = "获取用户统计数据", description = "获取用户的收藏数、浏览数、路线规划数等统计信息")
    @GetMapping("/{userId}")
    public ApiResponse<UserCountTable> getUserStats(@PathVariable Long userId) {
        log.info("查询用户统计数据 - userId: {}", userId);

        try {
            UserCountTable stats = userCountTableMapper.selectOne(
                    new LambdaQueryWrapper<UserCountTable>()
                            .eq(UserCountTable::getUserId, userId)
            );

            if (stats == null) {
                // 如果不存在记录，返回默认值
                stats = UserCountTable.builder()
                        .userId(userId)
                        .collectCount(0)
                        .browsingCount(0)
                        .planningCount(0)
                        .build();
            }

            return ApiResponse.success(stats);
        } catch (Exception e) {
            log.error("查询用户统计数据失败", e);
            return ApiResponse.error("查询统计数据失败: " + e.getMessage());
        }
    }
}
