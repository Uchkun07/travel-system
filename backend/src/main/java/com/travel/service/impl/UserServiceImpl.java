package com.travel.service.impl;

import com.travel.entity.User;
import com.travel.exception.UserAlreadyExistsException;
import com.travel.mapper.UserMapper;
import com.travel.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 用户服务实现类
 * 实现用户相关的业务逻辑
 * 
 * @author Travel System
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @Override
    public User getUserById(Long id) {
        if (id == null) {
            log.warn("getUserById: id参数为空");
            return null;
        }
        
        try {
            User user = userMapper.selectById(id);
            log.info("getUserById: 查询用户ID={}, 结果={}", id, user != null ? "找到" : "未找到");
            return user;
        } catch (Exception e) {
            log.error("getUserById: 查询用户失败, id={}", id, e);
            return null;
        }
    }
    
    @Override
    public User getUserByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            log.warn("getUserByUsername: username参数为空");
            return null;
        }
        
        try {
            User user = userMapper.selectByUsername(username.trim());
            log.info("getUserByUsername: 查询用户名={}, 结果={}", username, user != null ? "找到" : "未找到");
            return user;
        } catch (Exception e) {
            log.error("getUserByUsername: 查询用户失败, username={}", username, e);
            return null;
        }
    }
    
    @Override
    @Transactional
    public boolean createUser(User user) throws UserAlreadyExistsException {
        if (user == null) {
            log.warn("createUser: user参数为空");
            return false;
        }
        
        // 验证必填字段
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            log.warn("createUser: 用户名不能为空");
            return false;
        }
        
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            log.warn("createUser: 密码不能为空");
            return false;
        }
        
        String username = user.getUsername().trim();
        
        try {
            // 检查用户名是否已存在
            User existingUser = userMapper.selectByUsername(username);
            if (existingUser != null) {
                log.warn("createUser: 用户名已存在, username={}", username);
                throw new UserAlreadyExistsException("用户名已存在: " + username);
            }
            
            // 检查手机号是否已存在（如果提供了手机号）
            if (user.getPhone() != null && !user.getPhone().trim().isEmpty()) {
                User existingPhoneUser = userMapper.selectByPhone(user.getPhone().trim());
                if (existingPhoneUser != null) {
                    log.warn("createUser: 手机号已存在, phone={}", user.getPhone());
                    throw new UserAlreadyExistsException("手机号已存在: " + user.getPhone());
                }
            }
            
            // 设置默认值和加密密码
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setCreatedTime(LocalDateTime.now());
            user.setStatus(user.getStatus() != null ? user.getStatus() : User.Status.ENABLED.getCode());
            user.setRole(user.getRole() != null ? user.getRole() : User.Role.USER.getCode());
            
            // 清理电话和邮箱字段
            if (user.getPhone() != null) {
                user.setPhone(user.getPhone().trim().isEmpty() ? null : user.getPhone().trim());
            }
            if (user.getEmail() != null) {
                user.setEmail(user.getEmail().trim().isEmpty() ? null : user.getEmail().trim());
            }
            
            int result = userMapper.insert(user);
            boolean success = result > 0;
            
            log.info("createUser: 创建用户{}, username={}, id={}", 
                    success ? "成功" : "失败", username, user.getId());
            
            return success;
        } catch (UserAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            log.error("createUser: 创建用户失败, username={}", username, e);
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean updateUser(User user) {
        if (user == null || user.getId() == null) {
            log.warn("updateUser: user或user.id参数为空");
            return false;
        }
        
        try {
            // 检查用户是否存在
            User existingUser = userMapper.selectById(user.getId());
            if (existingUser == null) {
                log.warn("updateUser: 用户不存在, id={}", user.getId());
                return false;
            }
            
            // 如果更新手机号，检查是否与其他用户重复
            if (user.getPhone() != null && !user.getPhone().trim().isEmpty()) {
                User existingPhoneUser = userMapper.selectByPhone(user.getPhone().trim());
                if (existingPhoneUser != null && !existingPhoneUser.getId().equals(user.getId())) {
                    log.warn("updateUser: 手机号已被其他用户使用, phone={}", user.getPhone());
                    return false;
                }
            }
            
            // 设置更新时间
            user.setUpdatedTime(LocalDateTime.now());
            
            // 清理字段
            if (user.getPhone() != null) {
                user.setPhone(user.getPhone().trim().isEmpty() ? null : user.getPhone().trim());
            }
            if (user.getEmail() != null) {
                user.setEmail(user.getEmail().trim().isEmpty() ? null : user.getEmail().trim());
            }
            
            int result = userMapper.update(user);
            boolean success = result > 0;
            
            log.info("updateUser: 更新用户{}, id={}", success ? "成功" : "失败", user.getId());
            
            return success;
        } catch (Exception e) {
            log.error("updateUser: 更新用户失败, id={}", user.getId(), e);
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean deleteUser(Long id) {
        if (id == null) {
            log.warn("deleteUser: id参数为空");
            return false;
        }
        
        try {
            // 检查用户是否存在
            User existingUser = userMapper.selectById(id);
            if (existingUser == null) {
                log.warn("deleteUser: 用户不存在, id={}", id);
                return false;
            }
            
            int result = userMapper.deleteById(id, LocalDateTime.now());
            boolean success = result > 0;
            
            log.info("deleteUser: 删除用户{}, id={}", success ? "成功" : "失败", id);
            
            return success;
        } catch (Exception e) {
            log.error("deleteUser: 删除用户失败, id={}", id, e);
            return false;
        }
    }
    
    @Override
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            log.warn("verifyPassword: 密码参数为空");
            return false;
        }
        
        try {
            boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);
            log.debug("verifyPassword: 密码验证结果={}", matches);
            return matches;
        } catch (Exception e) {
            log.error("verifyPassword: 密码验证失败", e);
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean updateLastLoginTime(Long id) {
        if (id == null) {
            log.warn("updateLastLoginTime: id参数为空");
            return false;
        }
        
        try {
            LocalDateTime now = LocalDateTime.now();
            int result = userMapper.updateLastLoginTime(id, now, now);
            boolean success = result > 0;
            
            log.info("updateLastLoginTime: 更新最后登录时间{}, id={}", success ? "成功" : "失败", id);
            
            return success;
        } catch (Exception e) {
            log.error("updateLastLoginTime: 更新最后登录时间失败, id={}", id, e);
            return false;
        }
    }
}