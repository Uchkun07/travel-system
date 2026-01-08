package io.github.uchkun07.travelsystem.service;

import io.github.uchkun07.travelsystem.dto.BrowseRecordRequest;

/**
 * 用户浏览记录服务接口
 */
public interface IUserBrowseRecordService {

    /**
     * 记录用户浏览行为（埋点）
     *
     * @param request 浏览记录请求
     */
    void recordBrowse(BrowseRecordRequest request);
}
