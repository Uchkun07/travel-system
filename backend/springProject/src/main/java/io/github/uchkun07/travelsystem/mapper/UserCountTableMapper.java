package io.github.uchkun07.travelsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.uchkun07.travelsystem.entity.UserCountTable;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户计数表Mapper
 */
@Mapper
public interface UserCountTableMapper extends BaseMapper<UserCountTable> {

    /**
     * 增加收藏计数
     *
     * @param userId 用户ID
     * @param increment 增量（1或-1）
     * @return 影响行数
     */
    int incrementCollectCount(@Param("userId") Long userId, @Param("increment") Integer increment);

    /**
     * 增加浏览计数
     *
     * @param userId 用户ID
     * @param increment 增量（1或-1）
     * @return 影响行数
     */
    int incrementBrowsingCount(@Param("userId") Long userId, @Param("increment") Integer increment);

    /**
     * 增加路线规划计数
     *
     * @param userId 用户ID
     * @param increment 增量（1或-1）
     * @return 影响行数
     */
    int incrementPlanningCount(@Param("userId") Long userId, @Param("increment") Integer increment);
}

