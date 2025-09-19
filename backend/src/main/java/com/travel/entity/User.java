package com.travel.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 用户实体类
 * 映射数据库user表
 * 
 * @author Travel System
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    
    /**
     * 用户ID（主键）
     */
    private Long id;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 密码（BCrypt加密存储）
     */
    private String password;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 邮箱地址
     */
    private String email;
    
    /**
     * 注册时间
     */
    private LocalDateTime createdTime;
    
    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;
    
    /**
     * 用户状态：0-禁用，1-启用
     */
    private Integer status;
    
    /**
     * 用户角色：USER-普通用户，ADMIN-管理员
     */
    private String role;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
    
    /**
     * 用户状态枚举
     */
    public enum Status {
        DISABLED(0, "禁用"),
        ENABLED(1, "启用");
        
        private final Integer code;
        private final String description;
        
        Status(Integer code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public Integer getCode() {
            return code;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * 用户角色枚举
     */
    public enum Role {
        USER("USER", "普通用户"),
        ADMIN("ADMIN", "管理员");
        
        private final String code;
        private final String description;
        
        Role(String code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getDescription() {
            return description;
        }
    }
}