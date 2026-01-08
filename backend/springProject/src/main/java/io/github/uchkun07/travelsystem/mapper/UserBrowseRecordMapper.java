package io.github.uchkun07.travelsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.uchkun07.travelsystem.entity.UserBrowseRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
 * 用户浏览记录Mapper接口
 */
@Mapper
public interface UserBrowseRecordMapper extends BaseMapper<UserBrowseRecord> {

    /**
     * 查询指定时间内的浏览记录
     *
     * @param userId 用户ID
     * @param attractionId 景点ID
     * @param startTime 开始时间
     * @return 浏览记录
     */
    UserBrowseRecord selectRecentRecord(@Param("userId") Long userId,
                                        @Param("attractionId") Long attractionId,
                                        @Param("startTime") LocalDateTime startTime);

    /**
     * 累加浏览时长
     *
     * @param browseRecordId 浏览记录ID
     * @param additionalDuration 增加的时长
     * @return 影响行数
     */
    int addBrowseDuration(@Param("browseRecordId") Long browseRecordId,
                         @Param("additionalDuration") Integer additionalDuration);
}
