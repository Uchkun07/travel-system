package org.example.springproject.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.springproject.entity.UserAttractionCollection;

import java.util.List;

public interface IUserAttractionCollectionService extends IService<UserAttractionCollection> {

    boolean collect(Integer userId, Integer attractionId);

    boolean uncollect(Integer userId, Integer attractionId);

    boolean isCollected(Integer userId, Integer attractionId);

    List<Integer> getCollectedAttractionIds(Integer userId);
}


