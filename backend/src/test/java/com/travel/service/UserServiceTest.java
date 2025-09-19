package com.travel.service;

import com.travel.entity.User;
import com.travel.exception.UserAlreadyExistsException;
import com.travel.mapper.UserMapper;
import com.travel.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * UserService单元测试
 * 覆盖所有CRUD方法的测试用例
 * 
 * @author Travel System
 */
public class UserServiceTest {
    
    @Mock
    private UserMapper userMapper;
    
    @InjectMocks
    private UserServiceImpl userService;
    
    private BCryptPasswordEncoder passwordEncoder;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        passwordEncoder = new BCryptPasswordEncoder();
    }
    
    @Test
    void testGetUserById_Success() {
        // 准备测试数据
        Long userId = 1L;
        User mockUser = User.builder()
                .id(userId)
                .username("testuser")
                .email("test@example.com")
                .build();
        
        // 模拟数据库查询
        when(userMapper.selectById(userId)).thenReturn(mockUser);
        
        // 执行测试
        User result = userService.getUserById(userId);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("testuser", result.getUsername());
        verify(userMapper, times(1)).selectById(userId);
    }
    
    @Test
    void testGetUserById_NotFound() {
        // 准备测试数据
        Long userId = 999L;
        
        // 模拟数据库查询返回null
        when(userMapper.selectById(userId)).thenReturn(null);
        
        // 执行测试
        User result = userService.getUserById(userId);
        
        // 验证结果
        assertNull(result);
        verify(userMapper, times(1)).selectById(userId);
    }
    
    @Test
    void testGetUserById_NullParameter() {
        // 执行测试
        User result = userService.getUserById(null);
        
        // 验证结果
        assertNull(result);
        verify(userMapper, never()).selectById(any());
    }
    
    @Test
    void testGetUserByUsername_Success() {
        // 准备测试数据
        String username = "testuser";
        User mockUser = User.builder()
                .id(1L)
                .username(username)
                .email("test@example.com")
                .build();
        
        // 模拟数据库查询
        when(userMapper.selectByUsername(username)).thenReturn(mockUser);
        
        // 执行测试
        User result = userService.getUserByUsername(username);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        verify(userMapper, times(1)).selectByUsername(username);
    }
    
    @Test
    void testGetUserByUsername_NotFound() {
        // 准备测试数据
        String username = "nonexistent";
        
        // 模拟数据库查询返回null
        when(userMapper.selectByUsername(username)).thenReturn(null);
        
        // 执行测试
        User result = userService.getUserByUsername(username);
        
        // 验证结果
        assertNull(result);
        verify(userMapper, times(1)).selectByUsername(username);
    }
    
    @Test
    void testGetUserByUsername_NullParameter() {
        // 执行测试
        User result = userService.getUserByUsername(null);
        
        // 验证结果
        assertNull(result);
        verify(userMapper, never()).selectByUsername(any());
    }
    
    @Test
    void testCreateUser_Success() {
        // 准备测试数据
        User newUser = User.builder()
                .username("newuser")
                .password("password123")
                .phone("13800138000")
                .email("newuser@example.com")
                .build();
        
        // 模拟数据库查询（用户名和手机号都不存在）
        when(userMapper.selectByUsername("newuser")).thenReturn(null);
        when(userMapper.selectByPhone("13800138000")).thenReturn(null);
        when(userMapper.insert(any(User.class))).thenReturn(1);
        
        // 执行测试
        boolean result = userService.createUser(newUser);
        
        // 验证结果
        assertTrue(result);
        assertNotNull(newUser.getCreatedTime());
        assertEquals(User.Status.ENABLED.getCode(), newUser.getStatus());
        assertEquals(User.Role.USER.getCode(), newUser.getRole());
        assertTrue(passwordEncoder.matches("password123", newUser.getPassword()));
        
        verify(userMapper, times(1)).selectByUsername("newuser");
        verify(userMapper, times(1)).selectByPhone("13800138000");
        verify(userMapper, times(1)).insert(any(User.class));
    }
    
    @Test
    void testCreateUser_UsernameAlreadyExists() {
        // 准备测试数据
        User newUser = User.builder()
                .username("existinguser")
                .password("password123")
                .build();
        
        User existingUser = User.builder()
                .id(1L)
                .username("existinguser")
                .build();
        
        // 模拟数据库查询（用户名已存在）
        when(userMapper.selectByUsername("existinguser")).thenReturn(existingUser);
        
        // 执行测试并验证异常
        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.createUser(newUser)
        );
        
        assertTrue(exception.getMessage().contains("用户名已存在"));
        verify(userMapper, times(1)).selectByUsername("existinguser");
        verify(userMapper, never()).insert(any(User.class));
    }
    
    @Test
    void testCreateUser_PhoneAlreadyExists() {
        // 准备测试数据
        User newUser = User.builder()
                .username("newuser")
                .password("password123")
                .phone("13800138000")
                .build();
        
        User existingUser = User.builder()
                .id(1L)
                .username("otheruser")
                .phone("13800138000")
                .build();
        
        // 模拟数据库查询（用户名不存在，手机号已存在）
        when(userMapper.selectByUsername("newuser")).thenReturn(null);
        when(userMapper.selectByPhone("13800138000")).thenReturn(existingUser);
        
        // 执行测试并验证异常
        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.createUser(newUser)
        );
        
        assertTrue(exception.getMessage().contains("手机号已存在"));
        verify(userMapper, times(1)).selectByUsername("newuser");
        verify(userMapper, times(1)).selectByPhone("13800138000");
        verify(userMapper, never()).insert(any(User.class));
    }
    
    @Test
    void testCreateUser_NullParameter() {
        // 执行测试
        boolean result = userService.createUser(null);
        
        // 验证结果
        assertFalse(result);
        verify(userMapper, never()).insert(any(User.class));
    }
    
    @Test
    void testCreateUser_EmptyUsername() {
        // 准备测试数据
        User newUser = User.builder()
                .username("")
                .password("password123")
                .build();
        
        // 执行测试
        boolean result = userService.createUser(newUser);
        
        // 验证结果
        assertFalse(result);
        verify(userMapper, never()).insert(any(User.class));
    }
    
    @Test
    void testUpdateUser_Success() {
        // 准备测试数据
        User existingUser = User.builder()
                .id(1L)
                .username("testuser")
                .phone("13800138000")
                .build();
        
        User updateUser = User.builder()
                .id(1L)
                .phone("13800138001")
                .email("updated@example.com")
                .build();
        
        // 模拟数据库查询
        when(userMapper.selectById(1L)).thenReturn(existingUser);
        when(userMapper.selectByPhone("13800138001")).thenReturn(null);
        when(userMapper.update(any(User.class))).thenReturn(1);
        
        // 执行测试
        boolean result = userService.updateUser(updateUser);
        
        // 验证结果
        assertTrue(result);
        assertNotNull(updateUser.getUpdatedTime());
        verify(userMapper, times(1)).selectById(1L);
        verify(userMapper, times(1)).selectByPhone("13800138001");
        verify(userMapper, times(1)).update(any(User.class));
    }
    
    @Test
    void testUpdateUser_UserNotFound() {
        // 准备测试数据
        User updateUser = User.builder()
                .id(999L)
                .phone("13800138001")
                .build();
        
        // 模拟数据库查询（用户不存在）
        when(userMapper.selectById(999L)).thenReturn(null);
        
        // 执行测试
        boolean result = userService.updateUser(updateUser);
        
        // 验证结果
        assertFalse(result);
        verify(userMapper, times(1)).selectById(999L);
        verify(userMapper, never()).update(any(User.class));
    }
    
    @Test
    void testUpdateUser_PhoneAlreadyUsed() {
        // 准备测试数据
        User existingUser = User.builder()
                .id(1L)
                .username("testuser")
                .build();
        
        User anotherUser = User.builder()
                .id(2L)
                .phone("13800138001")
                .build();
        
        User updateUser = User.builder()
                .id(1L)
                .phone("13800138001")
                .build();
        
        // 模拟数据库查询
        when(userMapper.selectById(1L)).thenReturn(existingUser);
        when(userMapper.selectByPhone("13800138001")).thenReturn(anotherUser);
        
        // 执行测试
        boolean result = userService.updateUser(updateUser);
        
        // 验证结果
        assertFalse(result);
        verify(userMapper, times(1)).selectById(1L);
        verify(userMapper, times(1)).selectByPhone("13800138001");
        verify(userMapper, never()).update(any(User.class));
    }
    
    @Test
    void testDeleteUser_Success() {
        // 准备测试数据
        Long userId = 1L;
        User existingUser = User.builder()
                .id(userId)
                .username("testuser")
                .build();
        
        // 模拟数据库查询
        when(userMapper.selectById(userId)).thenReturn(existingUser);
        when(userMapper.deleteById(eq(userId), any(LocalDateTime.class))).thenReturn(1);
        
        // 执行测试
        boolean result = userService.deleteUser(userId);
        
        // 验证结果
        assertTrue(result);
        verify(userMapper, times(1)).selectById(userId);
        verify(userMapper, times(1)).deleteById(eq(userId), any(LocalDateTime.class));
    }
    
    @Test
    void testDeleteUser_UserNotFound() {
        // 准备测试数据
        Long userId = 999L;
        
        // 模拟数据库查询（用户不存在）
        when(userMapper.selectById(userId)).thenReturn(null);
        
        // 执行测试
        boolean result = userService.deleteUser(userId);
        
        // 验证结果
        assertFalse(result);
        verify(userMapper, times(1)).selectById(userId);
        verify(userMapper, never()).deleteById(any(), any());
    }
    
    @Test
    void testVerifyPassword_Success() {
        // 准备测试数据
        String rawPassword = "password123";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        
        // 执行测试
        boolean result = userService.verifyPassword(rawPassword, encodedPassword);
        
        // 验证结果
        assertTrue(result);
    }
    
    @Test
    void testVerifyPassword_Fail() {
        // 准备测试数据
        String rawPassword = "password123";
        String wrongPassword = "wrongpassword";
        String encodedPassword = passwordEncoder.encode(wrongPassword);
        
        // 执行测试
        boolean result = userService.verifyPassword(rawPassword, encodedPassword);
        
        // 验证结果
        assertFalse(result);
    }
    
    @Test
    void testVerifyPassword_NullParameters() {
        // 执行测试
        boolean result1 = userService.verifyPassword(null, "encoded");
        boolean result2 = userService.verifyPassword("raw", null);
        boolean result3 = userService.verifyPassword(null, null);
        
        // 验证结果
        assertFalse(result1);
        assertFalse(result2);
        assertFalse(result3);
    }
    
    @Test
    void testUpdateLastLoginTime_Success() {
        // 准备测试数据
        Long userId = 1L;
        
        // 模拟数据库更新
        when(userMapper.updateLastLoginTime(eq(userId), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(1);
        
        // 执行测试
        boolean result = userService.updateLastLoginTime(userId);
        
        // 验证结果
        assertTrue(result);
        verify(userMapper, times(1))
                .updateLastLoginTime(eq(userId), any(LocalDateTime.class), any(LocalDateTime.class));
    }
    
    @Test
    void testUpdateLastLoginTime_NullParameter() {
        // 执行测试
        boolean result = userService.updateLastLoginTime(null);
        
        // 验证结果
        assertFalse(result);
        verify(userMapper, never()).updateLastLoginTime(any(), any(), any());
    }
}