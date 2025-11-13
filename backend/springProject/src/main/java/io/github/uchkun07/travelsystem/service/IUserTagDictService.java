package io.github.uchkun07.travelsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.github.uchkun07.travelsystem.dto.*;
import io.github.uchkun07.travelsystem.entity.UserTagDict;

/**
 * 用户标签字典服务接口
 */
public interface IUserTagDictService extends IService<UserTagDict> {

    /**
     * 创建用户标签字典
     */
    UserTagDict createTagDict(UserTagDictRequest request);

    /**
     * 删除用户标签字典
     */
    void deleteTagDict(Integer tagDictId);

    /**
     * 批量删除用户标签字典
     */
    void batchDeleteTagDicts(java.util.List<Integer> tagDictIds);

    /**
     * 更新用户标签字典
     */
    UserTagDict updateTagDict(UserTagDictRequest request);

    /**
     * 分页查询用户标签字典
     */
    PageResponse<UserTagDict> queryTagDicts(UserTagDictQueryRequest request);

    /**
     * 根据ID查询用户标签字典
     */
    UserTagDict getTagDictById(Integer tagDictId);

    /**
     * 获取所有启用的标签字典
     */
    java.util.List<UserTagDict> getAllActiveTagDicts();
}
