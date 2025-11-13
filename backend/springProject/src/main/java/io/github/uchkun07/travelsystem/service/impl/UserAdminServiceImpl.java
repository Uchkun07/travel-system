package io.github.uchkun07.travelsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.uchkun07.travelsystem.dto.*;
import io.github.uchkun07.travelsystem.entity.*;
import io.github.uchkun07.travelsystem.mapper.*;
import io.github.uchkun07.travelsystem.service.IUserAdminService;
import io.github.uchkun07.travelsystem.service.IUserTagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户管理服务实现类（管理员侧）
 */
@Slf4j
@Service
public class UserAdminServiceImpl implements IUserAdminService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserProfileMapper userProfileMapper;

    @Autowired
    private UserPreferenceMapper userPreferenceMapper;

    @Autowired
    private UserCountTableMapper userCountTableMapper;

    @Autowired
    private UserTagMapper userTagMapper;

    @Autowired
    private IUserTagService userTagService;

    @Override
    public PageResponse<UserDetailResponse> queryUsers(UserQueryRequest request) {
        Page<User> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(request.getUsername())) {
            wrapper.like(User::getUsername, request.getUsername());
        }
        if (StringUtils.hasText(request.getNickname())) {
            wrapper.like(User::getNickname, request.getNickname());
        }
        if (StringUtils.hasText(request.getEmail())) {
            wrapper.eq(User::getEmail, request.getEmail());
        }
        if (request.getStatus() != null) {
            wrapper.eq(User::getStatus, request.getStatus());
        }
        
        // 如果指定了标签筛选
        if (request.getTagDictId() != null) {
            // 查询拥有该标签的用户ID列表
            LambdaQueryWrapper<UserTag> tagWrapper = new LambdaQueryWrapper<>();
            tagWrapper.eq(UserTag::getTagDictId, request.getTagDictId());
            List<UserTag> userTags = userTagMapper.selectList(tagWrapper);
            
            if (userTags.isEmpty()) {
                // 没有用户拥有该标签，返回空结果
                return PageResponse.<UserDetailResponse>builder()
                        .records(new ArrayList<>())
                        .total(0L)
                        .pageNum(request.getPageNum())
                        .pageSize(request.getPageSize())
                        .build();
            }
            
            List<Long> userIds = userTags.stream()
                    .map(UserTag::getUserId)
                    .distinct()
                    .collect(Collectors.toList());
            wrapper.in(User::getUserId, userIds);
        }
        
        // 根据手机号筛选（从UserProfile表）
        if (StringUtils.hasText(request.getPhone())) {
            LambdaQueryWrapper<UserProfile> profileWrapper = new LambdaQueryWrapper<>();
            profileWrapper.eq(UserProfile::getPhone, request.getPhone());
            List<UserProfile> profiles = userProfileMapper.selectList(profileWrapper);
            
            if (profiles.isEmpty()) {
                return PageResponse.<UserDetailResponse>builder()
                        .records(new ArrayList<>())
                        .total(0L)
                        .pageNum(request.getPageNum())
                        .pageSize(request.getPageSize())
                        .build();
            }
            
            List<Long> userIds = profiles.stream()
                    .map(UserProfile::getUserId)
                    .collect(Collectors.toList());
            wrapper.in(User::getUserId, userIds);
        }
        
        wrapper.orderByDesc(User::getCreateTime);
        
        Page<User> result = userMapper.selectPage(page, wrapper);
        
        // 构建详情响应
        List<UserDetailResponse> detailResponses = result.getRecords().stream()
                .map(this::buildUserDetailResponse)
                .collect(Collectors.toList());
        
        return PageResponse.<UserDetailResponse>builder()
                .records(detailResponses)
                .total(result.getTotal())
                .pageNum(result.getCurrent())
                .pageSize(result.getSize())
                .build();
    }

    @Override
    public UserDetailResponse getUserDetail(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        
        return buildUserDetailResponse(user);
    }

    @Override
    public UserCountTable getUserCountTable(Long userId) {
        LambdaQueryWrapper<UserCountTable> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCountTable::getUserId, userId);
        UserCountTable countTable = userCountTableMapper.selectOne(wrapper);
        
        if (countTable == null) {
            throw new IllegalArgumentException("用户计数表数据不存在");
        }
        
        return countTable;
    }

    /**
     * 构建用户详情响应
     */
    private UserDetailResponse buildUserDetailResponse(User user) {
        if (user == null) {
            throw new IllegalArgumentException("用户数据为空");
        }
        UserDetailResponse response = new UserDetailResponse();
        BeanUtils.copyProperties(user, response);
        
        // 查询用户Profile
        LambdaQueryWrapper<UserProfile> profileWrapper = new LambdaQueryWrapper<>();
        profileWrapper.eq(UserProfile::getUserId, user.getUserId());
        UserProfile profile = userProfileMapper.selectOne(profileWrapper);
        response.setProfile(profile);
        
        // 查询用户Preference
        LambdaQueryWrapper<UserPreference> preferenceWrapper = new LambdaQueryWrapper<>();
        preferenceWrapper.eq(UserPreference::getUserId, user.getUserId());
        UserPreference preference = userPreferenceMapper.selectOne(preferenceWrapper);
        response.setPreference(preference);
        
        // 查询用户标签
        List<UserTagDict> tags = userTagService.getUserTags(user.getUserId());
        response.setTags(tags);
        
        return response;
    }
}
