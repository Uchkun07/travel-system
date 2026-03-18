package io.github.uchkun07.travelsystem.mapper;

import io.github.uchkun07.travelsystem.dto.DashboardDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * Dashboard 数据大屏专用 Mapper（聚合查询，不继承 BaseMapper）
 */
@Mapper
public interface DashboardMapper {

    /**
     * 近 30 天每日新增用户数
     * 返回 Map { "date": "2026-03-01", "cnt": 18 }
     */
    @Select("SELECT DATE_FORMAT(create_time, '%m/%d') AS date, COUNT(*) AS cnt " +
            "FROM user " +
            "WHERE create_time >= DATE_SUB(CURDATE(), INTERVAL 29 DAY) " +
            "GROUP BY DATE(create_time), DATE_FORMAT(create_time, '%m/%d') " +
            "ORDER BY DATE(create_time)")
    List<Map<String, Object>> selectDailyNewUsers();

    /**
     * 近 30 天每日新增路线规划数
     * 返回 Map { "date": "2026-03-01", "cnt": 5 }
     */
    @Select("SELECT DATE_FORMAT(create_time, '%m/%d') AS date, COUNT(*) AS cnt " +
            "FROM travel_route " +
            "WHERE is_deleted = 0 AND create_time >= DATE_SUB(CURDATE(), INTERVAL 29 DAY) " +
            "GROUP BY DATE(create_time), DATE_FORMAT(create_time, '%m/%d') " +
            "ORDER BY DATE(create_time)")
    List<Map<String, Object>> selectDailyNewRoutes();

    /**
     * 热门景点 TOP 10（按 browse_count 降序）
     */
    @Select("SELECT name, browse_count AS views " +
            "FROM attraction " +
            "WHERE status = 1 " +
            "ORDER BY browse_count DESC " +
            "LIMIT 10")
    List<DashboardDTO.AttractionStat> selectTopAttractions();

    /**
     * 出行人群分布（来自 user_preference.travel_crowd）
     */
    @Select("SELECT travel_crowd AS name, COUNT(*) AS value " +
            "FROM user_preference " +
            "WHERE travel_crowd IS NOT NULL AND travel_crowd != '' " +
            "GROUP BY travel_crowd " +
            "ORDER BY value DESC")
    List<DashboardDTO.DistItem> selectGroupDist();

    /**
     * 出行方式分布（来自 travel_route.algorithm_params JSON 字段）
     */
    @Select("SELECT JSON_UNQUOTE(JSON_EXTRACT(algorithm_params, '$.travelMode')) AS name, " +
            "COUNT(*) AS value " +
            "FROM travel_route " +
            "WHERE is_deleted = 0 " +
            "  AND JSON_EXTRACT(algorithm_params, '$.travelMode') IS NOT NULL " +
            "GROUP BY JSON_UNQUOTE(JSON_EXTRACT(algorithm_params, '$.travelMode')) " +
            "ORDER BY value DESC")
    List<DashboardDTO.DistItem> selectModeDist();

    /**
     * 出行偏好分布（来自 travel_route.algorithm_params JSON 字段）
     */
    @Select("SELECT JSON_UNQUOTE(JSON_EXTRACT(algorithm_params, '$.travelPreference')) AS name, " +
            "COUNT(*) AS value " +
            "FROM travel_route " +
            "WHERE is_deleted = 0 " +
            "  AND JSON_EXTRACT(algorithm_params, '$.travelPreference') IS NOT NULL " +
            "GROUP BY JSON_UNQUOTE(JSON_EXTRACT(algorithm_params, '$.travelPreference')) " +
            "ORDER BY value DESC")
    List<DashboardDTO.DistItem> selectPrefDist();

    /**
     * 最近 10 条路线规划记录（关联 user 表获取用户名）
     */
    @Select("SELECT u.username AS username, " +
            "  JSON_UNQUOTE(JSON_EXTRACT(tr.algorithm_params, '$.departure'))       AS departure, " +
            "  JSON_UNQUOTE(JSON_EXTRACT(tr.algorithm_params, '$.travelMode'))      AS travelMode, " +
            "  JSON_UNQUOTE(JSON_EXTRACT(tr.algorithm_params, '$.travelGroup'))     AS travelGroup, " +
            "  IFNULL(tr.total_cost, JSON_UNQUOTE(JSON_EXTRACT(tr.algorithm_params, '$.budget'))) AS budget, " +
            "  DATE_FORMAT(tr.create_time, '%Y-%m-%d %H:%i:%s') AS createdAt " +
            "FROM travel_route tr " +
            "LEFT JOIN user u ON tr.user_id = u.user_id " +
            "WHERE tr.is_deleted = 0 " +
            "ORDER BY tr.create_time DESC " +
            "LIMIT 10")
    List<Map<String, Object>> selectRecentRoutes();
}
