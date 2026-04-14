package io.github.uchkun07.travelsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户统计数据响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsResponse {

    /**
     * 浏览景点数
     */
    private Integer browsingCount;

    /**
     * 收藏景点数
     */
    private Integer collectCount;

    /**
     * 路线规划数
     */
    private Integer planningCount;
}
