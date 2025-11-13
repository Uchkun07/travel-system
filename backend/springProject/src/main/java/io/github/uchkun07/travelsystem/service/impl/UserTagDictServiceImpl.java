package io.github.uchkun07.travelsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.uchkun07.travelsystem.dto.*;
import io.github.uchkun07.travelsystem.entity.UserTagDict;
import io.github.uchkun07.travelsystem.mapper.UserTagDictMapper;
import io.github.uchkun07.travelsystem.service.IUserTagDictService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 用户标签字典服务实现类
 */
@Slf4j
@Service
public class UserTagDictServiceImpl extends ServiceImpl<UserTagDictMapper, UserTagDict> implements IUserTagDictService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserTagDict createTagDict(UserTagDictRequest request) {
        // 检查标签名称是否已存在
        LambdaQueryWrapper<UserTagDict> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserTagDict::getTagName, request.getTagName());
        if (this.count(wrapper) > 0) {
            throw new IllegalArgumentException("标签名称已存在");
        }

        // 检查标签编码是否已存在
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserTagDict::getTagCode, request.getTagCode());
        if (this.count(wrapper) > 0) {
            throw new IllegalArgumentException("标签编码已存在");
        }

        UserTagDict tagDict = new UserTagDict();
        BeanUtils.copyProperties(request, tagDict);
        
        // 设置默认值
        if (tagDict.getStatus() == null) {
            tagDict.setStatus(1);
        }
        if (tagDict.getTagLevel() == null) {
            tagDict.setTagLevel(1);
        }
        
        this.save(tagDict);
        log.info("创建用户标签字典成功: tagDictId={}, tagName={}", tagDict.getTagDictId(), tagDict.getTagName());
        return tagDict;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTagDict(Integer tagDictId) {
        UserTagDict tagDict = this.getById(tagDictId);
        if (tagDict == null) {
            throw new IllegalArgumentException("标签字典不存在");
        }
        
        this.removeById(tagDictId);
        log.info("删除用户标签字典成功: tagDictId={}", tagDictId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteTagDicts(List<Integer> tagDictIds) {
        if (tagDictIds == null || tagDictIds.isEmpty()) {
            throw new IllegalArgumentException("标签字典ID列表不能为空");
        }
        
        this.removeByIds(tagDictIds);
        log.info("批量删除用户标签字典成功: count={}", tagDictIds.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserTagDict updateTagDict(UserTagDictRequest request) {
        if (request.getTagDictId() == null) {
            throw new IllegalArgumentException("标签字典ID不能为空");
        }
        
        UserTagDict tagDict = this.getById(request.getTagDictId());
        if (tagDict == null) {
            throw new IllegalArgumentException("标签字典不存在");
        }

        // 检查标签名称是否与其他记录重复
        if (StringUtils.hasText(request.getTagName()) && !request.getTagName().equals(tagDict.getTagName())) {
            LambdaQueryWrapper<UserTagDict> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserTagDict::getTagName, request.getTagName())
                   .ne(UserTagDict::getTagDictId, request.getTagDictId());
            if (this.count(wrapper) > 0) {
                throw new IllegalArgumentException("标签名称已存在");
            }
        }

        // 检查标签编码是否与其他记录重复
        if (StringUtils.hasText(request.getTagCode()) && !request.getTagCode().equals(tagDict.getTagCode())) {
            LambdaQueryWrapper<UserTagDict> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserTagDict::getTagCode, request.getTagCode())
                   .ne(UserTagDict::getTagDictId, request.getTagDictId());
            if (this.count(wrapper) > 0) {
                throw new IllegalArgumentException("标签编码已存在");
            }
        }

        BeanUtils.copyProperties(request, tagDict);
        this.updateById(tagDict);
        log.info("更新用户标签字典成功: tagDictId={}", tagDict.getTagDictId());
        return tagDict;
    }

    @Override
    public PageResponse<UserTagDict> queryTagDicts(UserTagDictQueryRequest request) {
        Page<UserTagDict> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<UserTagDict> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(request.getTagName())) {
            wrapper.like(UserTagDict::getTagName, request.getTagName());
        }
        if (StringUtils.hasText(request.getTagCode())) {
            wrapper.eq(UserTagDict::getTagCode, request.getTagCode());
        }
        if (request.getTagLevel() != null) {
            wrapper.eq(UserTagDict::getTagLevel, request.getTagLevel());
        }
        if (request.getStatus() != null) {
            wrapper.eq(UserTagDict::getStatus, request.getStatus());
        }
        
        wrapper.orderByDesc(UserTagDict::getSortOrder)
               .orderByDesc(UserTagDict::getCreateTime);
        
        Page<UserTagDict> result = this.page(page, wrapper);
        
        return PageResponse.<UserTagDict>builder()
                .records(result.getRecords())
                .total(result.getTotal())
                .pageNum(result.getCurrent())
                .pageSize(result.getSize())
                .build();
    }

    @Override
    public UserTagDict getTagDictById(Integer tagDictId) {
        UserTagDict tagDict = this.getById(tagDictId);
        if (tagDict == null) {
            throw new IllegalArgumentException("标签字典不存在");
        }
        return tagDict;
    }

    @Override
    public List<UserTagDict> getAllActiveTagDicts() {
        LambdaQueryWrapper<UserTagDict> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserTagDict::getStatus, 1)
               .orderByDesc(UserTagDict::getSortOrder)
               .orderByDesc(UserTagDict::getCreateTime);
        return this.list(wrapper);
    }
}
