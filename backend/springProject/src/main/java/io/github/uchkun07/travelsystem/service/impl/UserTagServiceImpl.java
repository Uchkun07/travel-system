package io.github.uchkun07.travelsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.uchkun07.travelsystem.dto.*;
import io.github.uchkun07.travelsystem.entity.UserTag;
import io.github.uchkun07.travelsystem.entity.UserTagDict;
import io.github.uchkun07.travelsystem.mapper.UserTagMapper;
import io.github.uchkun07.travelsystem.mapper.UserTagDictMapper;
import io.github.uchkun07.travelsystem.service.IUserTagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户标签关联服务实现类
 */
@Slf4j
@Service
public class UserTagServiceImpl extends ServiceImpl<UserTagMapper, UserTag> implements IUserTagService {

    @Autowired
    private UserTagDictMapper userTagDictMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindTag(UserTagBindRequest request) {
        // 检查标签字典是否存在
        UserTagDict tagDict = userTagDictMapper.selectById(request.getTagDictId());
        if (tagDict == null) {
            throw new IllegalArgumentException("标签字典不存在");
        }

        // 检查是否已经绑定
        LambdaQueryWrapper<UserTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserTag::getUserId, request.getUserId())
               .eq(UserTag::getTagDictId, request.getTagDictId());
        if (this.count(wrapper) > 0) {
            throw new IllegalArgumentException("用户已拥有该标签");
        }

        UserTag userTag = UserTag.builder()
                .userId(request.getUserId())
                .tagDictId(request.getTagDictId())
                .obtainTime(LocalDateTime.now())
                .build();
        
        this.save(userTag);
        log.info("用户绑定标签成功: userId={}, tagDictId={}", request.getUserId(), request.getTagDictId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unbindTag(UserTagBindRequest request) {
        LambdaQueryWrapper<UserTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserTag::getUserId, request.getUserId())
               .eq(UserTag::getTagDictId, request.getTagDictId());
        
        if (this.count(wrapper) == 0) {
            throw new IllegalArgumentException("用户未拥有该标签");
        }
        
        this.remove(wrapper);
        log.info("用户解绑标签成功: userId={}, tagDictId={}", request.getUserId(), request.getTagDictId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchBindTags(UserTagBatchBindRequest request) {
        if (request.getTagDictIds() == null || request.getTagDictIds().isEmpty()) {
            throw new IllegalArgumentException("标签字典ID列表不能为空");
        }

        // 检查所有标签字典是否存在
        LambdaQueryWrapper<UserTagDict> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.in(UserTagDict::getTagDictId, request.getTagDictIds());
        List<UserTagDict> tagDicts = userTagDictMapper.selectList(checkWrapper);
        if (tagDicts.size() != request.getTagDictIds().size()) {
            throw new IllegalArgumentException("部分标签字典不存在");
        }

        // 查询已绑定的标签
        LambdaQueryWrapper<UserTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserTag::getUserId, request.getUserId())
               .in(UserTag::getTagDictId, request.getTagDictIds());
        List<UserTag> existingTags = this.list(wrapper);
        List<Integer> existingTagDictIds = existingTags.stream()
                .map(UserTag::getTagDictId)
                .collect(Collectors.toList());

        // 过滤出需要新增的标签
        List<UserTag> newTags = new ArrayList<>();
        for (Integer tagDictId : request.getTagDictIds()) {
            if (!existingTagDictIds.contains(tagDictId)) {
                UserTag userTag = UserTag.builder()
                        .userId(request.getUserId())
                        .tagDictId(tagDictId)
                        .obtainTime(LocalDateTime.now())
                        .build();
                newTags.add(userTag);
            }
        }

        if (!newTags.isEmpty()) {
            this.saveBatch(newTags);
            log.info("用户批量绑定标签成功: userId={}, count={}", request.getUserId(), newTags.size());
        } else {
            log.info("用户已拥有所有标签，无需绑定: userId={}", request.getUserId());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUnbindTags(UserTagBatchBindRequest request) {
        if (request.getTagDictIds() == null || request.getTagDictIds().isEmpty()) {
            throw new IllegalArgumentException("标签字典ID列表不能为空");
        }

        LambdaQueryWrapper<UserTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserTag::getUserId, request.getUserId())
               .in(UserTag::getTagDictId, request.getTagDictIds());
        
        this.remove(wrapper);
        log.info("用户批量解绑标签成功: userId={}, tagDictIds={}", request.getUserId(), request.getTagDictIds());
    }

    @Override
    public List<UserTagDict> getUserTags(Long userId) {
        // 查询用户的标签关联
        LambdaQueryWrapper<UserTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserTag::getUserId, userId);
        List<UserTag> userTags = this.list(wrapper);

        if (userTags.isEmpty()) {
            return new ArrayList<>();
        }

        // 查询标签字典信息
        List<Integer> tagDictIds = userTags.stream()
                .map(UserTag::getTagDictId)
                .collect(Collectors.toList());
        
        LambdaQueryWrapper<UserTagDict> dictWrapper = new LambdaQueryWrapper<>();
        dictWrapper.in(UserTagDict::getTagDictId, tagDictIds);
        return userTagDictMapper.selectList(dictWrapper);
    }
}
