CREATE TABLE `user` (
  `user_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户唯一标识',
  `username` VARCHAR(50) DEFAULT NULL COMMENT '用户名(可空，用于传统登录)',
  `password_hash` VARCHAR(255) DEFAULT NULL COMMENT 'PBKDF2密码哈希',
  `salt` VARCHAR(64) DEFAULT NULL COMMENT 'PBKDF2盐值',
  `iterations` INT DEFAULT 100000 COMMENT 'PBKDF2迭代次数',
  
  -- 多方式登录标识
  `union_id` VARCHAR(100) DEFAULT NULL COMMENT '统一用户标识(用于微信/QQ绑定)',
  
  -- 基本信息
  `nickname` VARCHAR(100) DEFAULT NULL COMMENT '用户昵称',
  `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
  `gender` TINYINT DEFAULT 0 COMMENT '性别(0-未知,1-男,2-女)',
  `birthday` DATE DEFAULT NULL COMMENT '出生日期',
  `bio` VARCHAR(300) DEFAULT NULL COMMENT '个人简介',
  
  -- 账户状态
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态(0-禁用,1-正常,2-未激活)',
  `account_level` TINYINT DEFAULT 1 COMMENT '账户等级(1-普通,2-VIP,3-超级VIP)',
  `register_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` VARCHAR(45) DEFAULT NULL COMMENT '最后登录IP',
  
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_union_id` (`union_id`),
  KEY `idx_status` (`status`),
  KEY `idx_reg_time` (`register_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户主表';

CREATE TABLE `user_auth` (
  `auth_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '认证ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `auth_type` VARCHAR(20) NOT NULL COMMENT '认证类型(email,phone,wechat,qq,github)',
  `identifier` VARCHAR(100) NOT NULL COMMENT '认证标识(邮箱/手机号/第三方ID)',
  `credential` VARCHAR(500) DEFAULT NULL COMMENT '凭据(密码/Token)',
  `verified` TINYINT NOT NULL DEFAULT 0 COMMENT '是否已验证(0-未验证,1-已验证)',
  `verified_time` DATETIME DEFAULT NULL COMMENT '验证时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  PRIMARY KEY (`auth_id`),
  UNIQUE KEY `uk_identifier_type` (`identifier`, `auth_type`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_auth_type` (`auth_type`),
  CONSTRAINT `fk_auth_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户认证方式表';

CREATE TABLE `user_profile` (
  `profile_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '资料ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  
  -- 联系信息
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `wechat_openid` VARCHAR(100) DEFAULT NULL COMMENT '微信OpenID',
  `qq_openid` VARCHAR(100) DEFAULT NULL COMMENT 'QQ OpenID',
  
  -- 旅游偏好
  `preference_type` VARCHAR(255) DEFAULT NULL COMMENT '偏好旅游类型(海滩,文化,探险等)',
  `preference_destination` VARCHAR(255) DEFAULT NULL COMMENT '偏好目的地',
  `budget_range` VARCHAR(50) DEFAULT NULL COMMENT '预算范围',
  `travel_style` VARCHAR(100) DEFAULT NULL COMMENT '旅行风格',
  `favorite_season` VARCHAR(50) DEFAULT NULL COMMENT '偏好旅行季节',
  `companion_type` VARCHAR(50) DEFAULT NULL COMMENT '同行人类别',
  
  -- 其他信息
  `address` VARCHAR(255) DEFAULT NULL COMMENT '常住地址',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  
  PRIMARY KEY (`profile_id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  UNIQUE KEY `uk_email` (`email`),
  UNIQUE KEY `uk_phone` (`phone`),
  UNIQUE KEY `uk_wechat` (`wechat_openid`),
  UNIQUE KEY `uk_qq` (`qq_openid`),
  CONSTRAINT `fk_profile_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户资料表';

CREATE TABLE `social_auth` (
  `social_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '社交登录ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `platform` VARCHAR(20) NOT NULL COMMENT '平台(wechat,qq,github,google)',
  `open_id` VARCHAR(100) NOT NULL COMMENT '第三方平台用户ID',
  `union_id` VARCHAR(100) DEFAULT NULL COMMENT '联合ID(微信体系)',
  `access_token` VARCHAR(500) DEFAULT NULL COMMENT '访问令牌',
  `refresh_token` VARCHAR(500) DEFAULT NULL COMMENT '刷新令牌',
  `expire_time` DATETIME DEFAULT NULL COMMENT '令牌过期时间',
  `user_info` JSON DEFAULT NULL COMMENT '第三方用户信息(JSON格式)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '绑定时间',
  
  PRIMARY KEY (`social_id`),
  UNIQUE KEY `uk_platform_openid` (`platform`, `open_id`),
  UNIQUE KEY `uk_platform_unionid` (`platform`, `union_id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_social_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='第三方登录绑定表';