package org.example.springproject.service.impl;

import org.example.springproject.entity.Attraction;
import org.example.springproject.mapper.AttractionMapper;
import org.example.springproject.service.IAttractionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class AttractionServiceImpl extends ServiceImpl<AttractionMapper, Attraction> implements IAttractionService {

}
