package io.github.uchkun07.travelsystem.service;

/**
 * 用户计数服务接口
 */
public interface IUserCountService {

    /**
     * 增加收藏计数
     *
     * @param userId 用户ID
     */
    void incrementCollectCount(Long userId);

    /**
     * 减少收藏计数
     *
     * @param userId 用户ID
     */
    void decrementCollectCount(Long userId);

    /**
     * 增加浏览计数
     *
     * @param userId 用户ID
     */
    void incrementBrowsingCount(Long userId);

    /**
     * 增加路线规划计数
     *
     * @param userId 用户ID
     */
    void incrementPlanningCount(Long userId);
}
