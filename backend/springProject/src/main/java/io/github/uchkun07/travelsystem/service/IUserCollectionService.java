package io.github.uchkun07.travelsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.github.uchkun07.travelsystem.entity.UserCollection;

import java.util.List;

/**
 * 用户收藏服务接口
 */
public interface IUserCollectionService extends IService<UserCollection> {

    /**
     * 收藏景点
     *
     * @param userId 用户ID
     * @param attractionId 景点ID
     * @return 是否成功
     */
    boolean collectAttraction(Long userId, Long attractionId);

    /**
     * 取消收藏景点
     *
     * @param userId 用户ID
     * @param attractionId 景点ID
     * @return 是否成功
     */
    boolean uncollectAttraction(Long userId, Long attractionId);

    /**
     * 检查是否已收藏
     *
     * @param userId 用户ID
     * @param attractionId 景点ID
     * @return 是否已收藏
     */
    boolean isCollected(Long userId, Long attractionId);

    /**
     * 获取用户收藏的景点ID列表
     *
     * @param userId 用户ID
     * @return 景点ID列表
     */
    List<Long> getCollectedAttractionIds(Long userId);
}
