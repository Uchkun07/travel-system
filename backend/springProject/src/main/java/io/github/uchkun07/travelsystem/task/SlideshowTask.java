package io.github.uchkun07.travelsystem.task;

import io.github.uchkun07.travelsystem.service.ISlideshowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 轮播图定时任务
 */
@Slf4j
@Component
public class SlideshowTask {

    @Autowired
    private ISlideshowService slideshowService;

    /**
     * 每小时执行一次，检查并禁用过期的轮播图
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void disableExpiredSlideshows() {
        log.info("开始执行定时任务：禁用过期的轮播图");
        try {
            slideshowService.disableExpiredSlideshows();
            log.info("定时任务执行完成：禁用过期的轮播图");
        } catch (Exception e) {
            log.error("定时任务执行失败：禁用过期的轮播图", e);
        }
    }
}
