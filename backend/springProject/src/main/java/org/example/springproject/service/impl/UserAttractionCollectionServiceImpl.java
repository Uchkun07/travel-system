package org.example.springproject.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.springproject.entity.UserAttractionCollection;
import org.example.springproject.mapper.UserAttractionCollectionMapper;
import org.example.springproject.service.IUserAttractionCollectionService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserAttractionCollectionServiceImpl extends ServiceImpl<UserAttractionCollectionMapper, UserAttractionCollection>
        implements IUserAttractionCollectionService {

    @Override
    public boolean collect(Integer userId, Integer attractionId) {
        // 如果存在被逻辑删除的记录，则恢复；否则新增
        LambdaQueryWrapper<UserAttractionCollection> query = new LambdaQueryWrapper<UserAttractionCollection>()
                .eq(UserAttractionCollection::getUserId, userId)
                .eq(UserAttractionCollection::getAttractionId, attractionId);

        UserAttractionCollection existing = getOne(query, false);
        if (existing != null) {
            // 如果已存在且未删除，视为幂等成功
            if (existing.getIsDeleted() != null && existing.getIsDeleted() == 0) {
                return true;
            }
            // 恢复逻辑删除
            LambdaUpdateWrapper<UserAttractionCollection> restore = new LambdaUpdateWrapper<UserAttractionCollection>()
                    .eq(UserAttractionCollection::getUserId, userId)
                    .eq(UserAttractionCollection::getAttractionId, attractionId)
                    .set(UserAttractionCollection::getIsDeleted, 0)
                    .set(UserAttractionCollection::getCollectTime, LocalDateTime.now());
            return update(restore);
        }

        UserAttractionCollection record = new UserAttractionCollection();
        record.setUserId(userId);
        record.setAttractionId(attractionId);
        record.setCollectTime(LocalDateTime.now());
        record.setIsDeleted(0);
        return save(record);
    }

    @Override
    public boolean uncollect(Integer userId, Integer attractionId) {
        LambdaUpdateWrapper<UserAttractionCollection> update = new LambdaUpdateWrapper<UserAttractionCollection>()
                .eq(UserAttractionCollection::getUserId, userId)
                .eq(UserAttractionCollection::getAttractionId, attractionId)
                .eq(UserAttractionCollection::getIsDeleted, 0)
                .set(UserAttractionCollection::getIsDeleted, 1);
        return update(update);
    }

    @Override
    public boolean isCollected(Integer userId, Integer attractionId) {
        return count(new LambdaQueryWrapper<UserAttractionCollection>()
                .eq(UserAttractionCollection::getUserId, userId)
                .eq(UserAttractionCollection::getAttractionId, attractionId)
                .eq(UserAttractionCollection::getIsDeleted, 0)) > 0;
    }

    @Override
    public List<Integer> getCollectedAttractionIds(Integer userId) {
        List<UserAttractionCollection> list = list(new LambdaQueryWrapper<UserAttractionCollection>()
                .eq(UserAttractionCollection::getUserId, userId)
                .eq(UserAttractionCollection::getIsDeleted, 0)
                .orderByDesc(UserAttractionCollection::getCollectTime));
        return list.stream()
                .map(UserAttractionCollection::getAttractionId)
                .collect(Collectors.toList());
    }
}


