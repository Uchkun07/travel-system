package org.example.springproject.service.impl;

import org.example.springproject.entity.User;
import org.example.springproject.mapper.UserMapper;
import org.example.springproject.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    
}
