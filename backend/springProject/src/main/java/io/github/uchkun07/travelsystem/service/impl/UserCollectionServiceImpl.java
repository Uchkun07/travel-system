package io.github.uchkun07.travelsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.uchkun07.travelsystem.entity.UserCollection;
import io.github.uchkun07.travelsystem.mapper.UserCollectionMapper;
import io.github.uchkun07.travelsystem.service.IUserCollectionService;
import io.github.uchkun07.travelsystem.service.IUserCountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户收藏服务实现类
 */
@Slf4j
@Service
public class UserCollectionServiceImpl extends ServiceImpl<UserCollectionMapper, UserCollection>
        implements IUserCollectionService {

    @Autowired
    private IUserCountService userCountService;

    @Override
    public boolean collectAttraction(Long userId, Long attractionId) {
        // 检查是否已存在记录（包括已删除的）
        LambdaQueryWrapper<UserCollection> query = new LambdaQueryWrapper<UserCollection>()
                .eq(UserCollection::getUserId, userId)
                .eq(UserCollection::getAttractionId, attractionId);

        // 查询时忽略逻辑删除，使用 .apply() 来绕过 @TableLogic
        UserCollection existing = baseMapper.selectOne(query.last("LIMIT 1"));
        
        if (existing != null) {
            // 如果已存在且未删除，视为幂等成功
            if (existing.getIsDeleted() != null && existing.getIsDeleted() == 0) {
                log.info("景点已收藏，userId: {}, attractionId: {}", userId, attractionId);
                return true;
            }
            // 恢复逻辑删除的记录
            LambdaUpdateWrapper<UserCollection> restore = new LambdaUpdateWrapper<UserCollection>()
                    .eq(UserCollection::getUserId, userId)
                    .eq(UserCollection::getAttractionId, attractionId)
                    .set(UserCollection::getIsDeleted, 0)
                    .set(UserCollection::getCollectionTime, LocalDateTime.now());
            boolean updated = update(restore);
            log.info("恢复收藏记录，userId: {}, attractionId: {}, result: {}", userId, attractionId, updated);
            if (updated) {
                // 恢复收藏成功，增加计数
                userCountService.incrementCollectCount(userId);
            }
            return updated;
        }

        // 创建新的收藏记录
        UserCollection record = new UserCollection();
        record.setUserId(userId);
        record.setAttractionId(attractionId);
        record.setCollectionTime(LocalDateTime.now());
        record.setIsDeleted(0);
        boolean saved = save(record);
        log.info("新增收藏记录，userId: {}, attractionId: {}, result: {}", userId, attractionId, saved);
        if (saved) {
            // 新增收藏成功，增加计数
            userCountService.incrementCollectCount(userId);
        }
        return saved;
    }

    @Override
    public boolean uncollectAttraction(Long userId, Long attractionId) {
        // 逻辑删除
        LambdaUpdateWrapper<UserCollection> update = new LambdaUpdateWrapper<UserCollection>()
                .eq(UserCollection::getUserId, userId)
                .eq(UserCollection::getAttractionId, attractionId)
                .eq(UserCollection::getIsDeleted, 0)
                .set(UserCollection::getIsDeleted, 1);
        boolean updated = update(update);
        log.info("取消收藏，userId: {}, attractionId: {}, result: {}", userId, attractionId, updated);
        if (updated) {
            // 取消收藏成功，减少计数
            userCountService.decrementCollectCount(userId);
        }
        return updated;
    }

    @Override
    public boolean isCollected(Long userId, Long attractionId) {
        long count = count(new LambdaQueryWrapper<UserCollection>()
                .eq(UserCollection::getUserId, userId)
                .eq(UserCollection::getAttractionId, attractionId)
                .eq(UserCollection::getIsDeleted, 0));
        return count > 0;
    }

    @Override
    public List<Long> getCollectedAttractionIds(Long userId) {
        List<UserCollection> list = list(new LambdaQueryWrapper<UserCollection>()
                .eq(UserCollection::getUserId, userId)
                .eq(UserCollection::getIsDeleted, 0)
                .orderByDesc(UserCollection::getCollectionTime));
        return list.stream()
                .map(UserCollection::getAttractionId)
                .collect(Collectors.toList());
    }
}
