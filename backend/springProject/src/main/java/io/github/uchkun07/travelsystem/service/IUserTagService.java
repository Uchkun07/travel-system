package io.github.uchkun07.travelsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.github.uchkun07.travelsystem.dto.*;
import io.github.uchkun07.travelsystem.entity.UserTag;
import io.github.uchkun07.travelsystem.entity.UserTagDict;

/**
 * 用户标签关联服务接口
 */
public interface IUserTagService extends IService<UserTag> {

    /**
     * 绑定用户标签
     */
    void bindTag(UserTagBindRequest request);

    /**
     * 解绑用户标签
     */
    void unbindTag(UserTagBindRequest request);

    /**
     * 批量绑定用户标签
     */
    void batchBindTags(UserTagBatchBindRequest request);

    /**
     * 批量解绑用户标签
     */
    void batchUnbindTags(UserTagBatchBindRequest request);

    /**
     * 获取用户的所有标签
     */
    java.util.List<UserTagDict> getUserTags(Long userId);
}
