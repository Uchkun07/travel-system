package io.github.uchkun07.travelsystem.service.impl;

import io.github.uchkun07.travelsystem.mapper.UserCountTableMapper;
import io.github.uchkun07.travelsystem.service.IUserCountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户计数服务实现类
 */
@Slf4j
@Service
public class UserCountServiceImpl implements IUserCountService {

    @Autowired
    private UserCountTableMapper userCountTableMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementCollectCount(Long userId) {
        int updated = userCountTableMapper.incrementCollectCount(userId, 1);
        log.info("用户收藏计数+1 - userId: {}, 影响行数: {}", userId, updated);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void decrementCollectCount(Long userId) {
        int updated = userCountTableMapper.incrementCollectCount(userId, -1);
        log.info("用户收藏计数-1 - userId: {}, 影响行数: {}", userId, updated);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementBrowsingCount(Long userId) {
        int updated = userCountTableMapper.incrementBrowsingCount(userId, 1);
        log.info("用户浏览计数+1 - userId: {}, 影响行数: {}", userId, updated);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementPlanningCount(Long userId) {
        int updated = userCountTableMapper.incrementPlanningCount(userId, 1);
        log.info("用户路线规划计数+1 - userId: {}, 影响行数: {}", userId, updated);
    }
}
