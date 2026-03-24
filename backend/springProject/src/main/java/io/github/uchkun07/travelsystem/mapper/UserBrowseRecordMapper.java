package io.github.uchkun07.travelsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.uchkun07.travelsystem.dto.RecommendTypeBehaviorStat;
import io.github.uchkun07.travelsystem.entity.UserBrowseRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserBrowseRecordMapper extends BaseMapper<UserBrowseRecord> {

    /**
     * 按景点类型聚合用户近N天行为（兼容旧表结构：事件元数据写入 device_info JSON）
     */
    @Select("SELECT a.type_id AS typeId, " +
            "SUM(CASE WHEN LOCATE('et=click', IFNULL(ubr.device_info, '')) > 0 THEN 1 ELSE 0 END) AS clickCount, " +
            "SUM(CASE WHEN LOCATE('et=stay', IFNULL(ubr.device_info, '')) > 0 " +
            "THEN IFNULL(ubr.browse_duration, 0) ELSE 0 END) AS staySeconds " +
            "FROM user_browse_record ubr " +
            "JOIN attraction a ON a.attraction_id = ubr.attraction_id " +
            "WHERE ubr.user_id = #{userId} " +
            "AND ubr.browse_time >= DATE_SUB(NOW(), INTERVAL #{days} DAY) " +
            "GROUP BY a.type_id")
    List<RecommendTypeBehaviorStat> aggregateUserBehaviorByType(@Param("userId") Long userId,
                                                                @Param("days") Integer days);

    @Select("SELECT COUNT(*) FROM user_browse_record ubr " +
            "WHERE ubr.user_id = #{userId} " +
            "AND ubr.browse_time >= DATE_SUB(NOW(), INTERVAL #{days} DAY) " +
            "AND (LOCATE('et=click', IFNULL(ubr.device_info, '')) > 0 OR LOCATE('et=stay', IFNULL(ubr.device_info, '')) > 0)")
    Long countRecentBehaviorEvents(@Param("userId") Long userId,
                                   @Param("days") Integer days);
}