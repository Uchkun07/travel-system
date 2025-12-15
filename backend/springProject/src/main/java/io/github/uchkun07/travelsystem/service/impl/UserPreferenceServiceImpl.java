package io.github.uchkun07.travelsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.uchkun07.travelsystem.dto.UserPreferenceRequest;
import io.github.uchkun07.travelsystem.dto.UserPreferenceResponse;
import io.github.uchkun07.travelsystem.entity.AttractionType;
import io.github.uchkun07.travelsystem.entity.UserPreference;
import io.github.uchkun07.travelsystem.mapper.AttractionTypeMapper;
import io.github.uchkun07.travelsystem.mapper.UserPreferenceMapper;
import io.github.uchkun07.travelsystem.service.IUserPreferenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户偏好服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserPreferenceServiceImpl implements IUserPreferenceService {

    private final UserPreferenceMapper userPreferenceMapper;
    private final AttractionTypeMapper attractionTypeMapper;

    @Override
    public UserPreferenceResponse getUserPreference(Long userId) {
        LambdaQueryWrapper<UserPreference> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPreference::getUserId, userId);
        UserPreference preference = userPreferenceMapper.selectOne(wrapper);

        if (preference == null) {
            throw new IllegalArgumentException("用户偏好不存在");
        }

        return buildResponse(preference);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserPreferenceResponse updateUserPreference(Long userId, UserPreferenceRequest request) {
        LambdaQueryWrapper<UserPreference> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPreference::getUserId, userId);
        UserPreference preference = userPreferenceMapper.selectOne(wrapper);

        if (preference == null) {
            throw new IllegalArgumentException("用户偏好不存在");
        }

        // 更新偏好信息
        if (request.getPreferAttractionTypeId() != null) {
            preference.setPreferAttractionTypeId(request.getPreferAttractionTypeId());
        }
        if (request.getBudgetFloor() != null) {
            preference.setBudgetFloor(request.getBudgetFloor());
        }
        if (request.getBudgetRange() != null) {
            preference.setBudgetRange(request.getBudgetRange());
        }
        if (request.getTravelCrowd() != null) {
            preference.setTravelCrowd(request.getTravelCrowd());
        }
        if (request.getPreferSeason() != null) {
            preference.setPreferSeason(request.getPreferSeason());
        }

        userPreferenceMapper.updateById(preference);
        log.info("用户 {} 更新偏好成功", userId);

        return buildResponse(preference);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUserPreference(Long userId) {
        LambdaQueryWrapper<UserPreference> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPreference::getUserId, userId);
        userPreferenceMapper.delete(wrapper);
        log.info("用户 {} 偏好已删除", userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void initializeUserPreference(Long userId) {
        // 检查是否已存在
        LambdaQueryWrapper<UserPreference> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPreference::getUserId, userId);
        if (userPreferenceMapper.selectCount(wrapper) > 0) {
            return;
        }

        // 创建空的偏好记录
        UserPreference preference = UserPreference.builder()
                .userId(userId)
                .build();
        userPreferenceMapper.insert(preference);
        log.info("用户 {} 偏好初始化成功", userId);
    }

    /**
     * 构建响应对象
     */
    private UserPreferenceResponse buildResponse(UserPreference preference) {
        UserPreferenceResponse.UserPreferenceResponseBuilder builder = UserPreferenceResponse.builder()
                .preferenceId(preference.getPreferenceId())
                .userId(preference.getUserId())
                .preferAttractionTypeId(preference.getPreferAttractionTypeId())
                .budgetFloor(preference.getBudgetFloor())
                .budgetRange(preference.getBudgetRange())
                .travelCrowd(preference.getTravelCrowd())
                .preferSeason(preference.getPreferSeason())
                .createTime(preference.getCreateTime())
                .updateTime(preference.getUpdateTime());

        // 如果有景点类型ID，查询类型名称
        if (preference.getPreferAttractionTypeId() != null) {
            AttractionType type = attractionTypeMapper.selectById(preference.getPreferAttractionTypeId());
            if (type != null) {
                builder.attractionTypeName(type.getTypeName());
            }
        }

        return builder.build();
    }
}
