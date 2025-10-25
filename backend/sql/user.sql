CREATE TABLE `user` (
  `user_id` int NOT NULL AUTO_INCREMENT COMMENT '用户唯一标识',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password_hash` varchar(256) NOT NULL COMMENT 'PBKDF2密码哈希',
  `salt` varchar(64) NOT NULL COMMENT 'PBKDF2盐值(32字节hex)',
  `iterations` int NOT NULL DEFAULT 100000 COMMENT 'PBKDF2迭代次数',
  `email` varchar(100) NOT NULL COMMENT '电子邮箱',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号码',
  `full_name` varchar(100) DEFAULT NULL COMMENT '真实姓名',
  `gender` tinyint DEFAULT 0 COMMENT '性别(0-未知,1-男,2-女)',
  `birthday` date DEFAULT NULL COMMENT '出生日期',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `address` varchar(255) DEFAULT NULL COMMENT '常住地址',
  `preference_type` varchar(255) DEFAULT NULL COMMENT '偏好旅游类型',
  `preference_destination` varchar(255) DEFAULT NULL COMMENT '偏好目的地',
  `budget_range` varchar(50) DEFAULT NULL COMMENT '预算范围',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态(0-禁用,1-正常)',
  `register_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`),
  UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

ALTER TABLE `user` 
ADD COLUMN `union_id` VARCHAR(100) DEFAULT NULL COMMENT '统一用户标识(用于跨平台用户识别)',
ADD COLUMN `register_source` VARCHAR(20) DEFAULT 'email' COMMENT '注册来源: email,wechat,qq,phone',
ADD UNIQUE KEY `uk_union_id` (`union_id`),
ADD KEY `idx_register_source` (`register_source`);

CREATE TABLE `social_auth` (
  `auth_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '授权ID',
  `user_id` INT NOT NULL COMMENT '用户ID(关联user表)',
  
  -- 平台基本信息
  `platform` VARCHAR(20) NOT NULL COMMENT '平台类型: wechat,qq,weibo,github,google,apple',
  `open_id` VARCHAR(100) NOT NULL COMMENT '第三方平台用户唯一ID',
  `union_id` VARCHAR(100) DEFAULT NULL COMMENT '联合ID(主要用于微信生态)',
  
  -- 授权令牌信息
  `access_token` VARCHAR(500) DEFAULT NULL COMMENT '访问令牌',
  `refresh_token` VARCHAR(500) DEFAULT NULL COMMENT '刷新令牌',
  `token_expire_time` DATETIME DEFAULT NULL COMMENT '令牌过期时间',
  `scope` VARCHAR(200) DEFAULT NULL COMMENT '授权范围',
  
  -- 第三方平台用户信息
  `social_nickname` VARCHAR(100) DEFAULT NULL COMMENT '第三方平台昵称',
  `social_avatar` VARCHAR(500) DEFAULT NULL COMMENT '第三方平台头像URL',
  `social_gender` TINYINT DEFAULT 0 COMMENT '第三方平台性别(0-未知,1-男,2-女)',
  `social_location` VARCHAR(100) DEFAULT NULL COMMENT '第三方平台地区',
  `social_profile` JSON DEFAULT NULL COMMENT '第三方平台完整用户信息(JSON格式)',
  
  -- 状态管理
  `is_bound` TINYINT NOT NULL DEFAULT 1 COMMENT '绑定状态(0-已解绑,1-已绑定)',
  `is_primary` TINYINT NOT NULL DEFAULT 0 COMMENT '是否主登录方式(0-否,1-是)',
  
  -- 时间信息
  `bind_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '绑定时间',
  `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  
  PRIMARY KEY (`auth_id`),
  UNIQUE KEY `uk_platform_openid` (`platform`, `open_id`),
  UNIQUE KEY `uk_platform_unionid` (`platform`, `union_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_platform` (`platform`),
  KEY `idx_union_id` (`union_id`),
  KEY `idx_is_bound` (`is_bound`),
  KEY `idx_last_login` (`last_login_time`),
  CONSTRAINT `fk_social_auth_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='第三方登录授权表';