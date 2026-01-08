package io.github.uchkun07.travelsystem.service.impl;

import io.github.uchkun07.travelsystem.dto.BrowseRecordRequest;
import io.github.uchkun07.travelsystem.entity.UserBrowseRecord;
import io.github.uchkun07.travelsystem.mapper.UserBrowseRecordMapper;
import io.github.uchkun07.travelsystem.service.IUserBrowseRecordService;
import io.github.uchkun07.travelsystem.service.IUserCountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 用户浏览记录服务实现类
 */
@Slf4j
@Service
public class UserBrowseRecordServiceImpl implements IUserBrowseRecordService {

    @Autowired
    private UserBrowseRecordMapper userBrowseRecordMapper;

    @Autowired
    private IUserCountService userCountService;

    /**
     * 10分钟的时间间隔（分钟）
     */
    private static final int TIME_WINDOW_MINUTES = 10;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordBrowse(BrowseRecordRequest request) {
        log.info("记录浏览行为 - 用户ID: {}, 景点ID: {}, 时长: {}秒",
                request.getUserId(), request.getAttractionId(), request.getBrowseDuration());

        // 计算10分钟前的时间
        LocalDateTime tenMinutesAgo = LocalDateTime.now().minusMinutes(TIME_WINDOW_MINUTES);

        // 查询10分钟内是否存在该用户-景点的浏览记录
        UserBrowseRecord recentRecord = userBrowseRecordMapper.selectRecentRecord(
                request.getUserId(),
                request.getAttractionId(),
                tenMinutesAgo
        );

        if (recentRecord != null) {
            // 存在记录，累加浏览时长
            log.info("发现10分钟内的浏览记录ID: {}，累加时长", recentRecord.getBrowseRecordId());
            int updated = userBrowseRecordMapper.addBrowseDuration(
                    recentRecord.getBrowseRecordId(),
                    request.getBrowseDuration()
            );
            log.info("累加浏览时长完成，影响行数: {}", updated);
        } else {
            // 不存在记录，新增
            log.info("未发现10分钟内的浏览记录，新增记录");
            UserBrowseRecord newRecord = UserBrowseRecord.builder()
                    .userId(request.getUserId())
                    .attractionId(request.getAttractionId())
                    .browseDuration(request.getBrowseDuration())
                    .browseTime(LocalDateTime.now())
                    .deviceInfo(request.getDeviceInfo())
                    .build();

            userBrowseRecordMapper.insert(newRecord);
            log.info("新增浏览记录成功，记录ID: {}", newRecord.getBrowseRecordId());
            
            // 新增浏览记录成功，增加浏览计数
            userCountService.incrementBrowsingCount(request.getUserId());
        }
    }
}
