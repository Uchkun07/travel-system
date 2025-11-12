-- 创建用户账号表（user）
CREATE TABLE `user` (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户唯一标识（C端用户核心ID）',
  `username` varchar(50) NOT NULL COMMENT '用户名（登录凭证，支持自定义，不可重复）',
  `email` varchar(100) NOT NULL COMMENT '绑定邮箱（登录凭证+接收验证码，不可重复）',
  `password` varchar(100) NOT NULL COMMENT '加密密码（PBKDF2算法加盐存储，不存储明文）',
  `password_salt` varchar(100) NOT NULL COMMENT '密码盐值（随机字符串，PBKDF2加密时与密码拼接）',
  `pbkdf2_iterations` int NOT NULL DEFAULT 10000 COMMENT 'PBKDF2迭代次数（固定值，加密/校验统一使用）',
  `avatar_url` varchar(255) DEFAULT NULL COMMENT '用户头像URL（默认使用系统占位图）',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '账号状态（1=正常，0=禁用）',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '账号创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '账号信息更新时间',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户账号表';

-- 创建用户基本信息表（user_profile）
CREATE TABLE `user_profile` (
  `profile_id` bigint NOT NULL AUTO_INCREMENT COMMENT '基本信息唯一标识',
  `user_id` bigint NOT NULL COMMENT '关联用户账号表',
  `full_name` varchar(50) DEFAULT NULL COMMENT '用户真实姓名',
  `phone` varchar(20) DEFAULT NULL COMMENT '绑定手机号（用于安全验证）',
  `gender` tinyint DEFAULT NULL COMMENT '性别（0=未知，1=男，2=女）',
  `birthday` date DEFAULT NULL COMMENT '出生日期',
  `resident_address` varchar(255) DEFAULT NULL COMMENT '常驻地址',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '信息创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '信息更新时间',
  PRIMARY KEY (`profile_id`),
  UNIQUE KEY `uk_phone` (`phone`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_profile_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户基本信息表';

-- 创建景点类型表（attraction_type）
CREATE TABLE `attraction_type` (
  `type_id` int NOT NULL AUTO_INCREMENT COMMENT '类型唯一标识',
  `type_name` varchar(50) NOT NULL COMMENT '类型名称（不可重复）',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '展示排序序号',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（1=启用，0=禁用）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`type_id`),
  UNIQUE KEY `uk_type_name` (`type_name`),
  KEY `idx_status` (`status`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='景点类型表';

-- 创建用户偏好表（user_preference）
CREATE TABLE `user_preference` (
  `preference_id` bigint NOT NULL AUTO_INCREMENT COMMENT '偏好唯一标识',
  `user_id` bigint NOT NULL COMMENT '关联用户账号表',
  `prefer_attraction_type_id` int DEFAULT NULL COMMENT '偏好景点类型ID（关联attraction_type表）',
  `Budget_floor` int DEFAULT NULL COMMENT '预算下限',
  `budget_range` int DEFAULT NULL COMMENT '预算上限',
  `travel_crowd` varchar(50) DEFAULT NULL COMMENT '出行人群（如“独自出行”“情侣”“亲子”）',
  `prefer_season` varchar(50) DEFAULT NULL COMMENT '偏好出行季节',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '偏好创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '偏好更新时间',
  PRIMARY KEY (`preference_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_prefer_type` (`prefer_attraction_type_id`),
  CONSTRAINT `fk_preference_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_preference_type` FOREIGN KEY (`prefer_attraction_type_id`) REFERENCES `attraction_type` (`type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户偏好表';

-- 创建用户标签字典表（user_tag_dict）
CREATE TABLE `user_tag_dict` (
  `tag_dict_id` int NOT NULL AUTO_INCREMENT COMMENT '标签字典唯一标识',
  `tag_name` varchar(50) NOT NULL COMMENT '标签名称（系统唯一）',
  `tag_code` varchar(50) NOT NULL COMMENT '标签编码（代码中统一引用）',
  `tag_level` tinyint NOT NULL DEFAULT 1 COMMENT '标签等级（1=初级，2=中级，3=高级）',
  `trigger_condition` text NOT NULL COMMENT '标签触发条件（JSON格式）',
  `description` varchar(255) DEFAULT NULL COMMENT '标签描述',
  `icon_url` varchar(255) DEFAULT NULL COMMENT '标签图标URL',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '标签状态（1=启用，0=禁用）',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '展示排序序号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '标签创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '标签更新时间',
  PRIMARY KEY (`tag_dict_id`),
  UNIQUE KEY `uk_tag_name` (`tag_name`),
  UNIQUE KEY `uk_tag_code` (`tag_code`),
  KEY `idx_status` (`status`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户标签字典表（统一管理所有用户标签）';

-- 创建用户标签表（user_tag）
CREATE TABLE `user_tag` (
  `user_tag_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户-标签关联唯一标识',
  `user_id` bigint NOT NULL COMMENT '关联用户账号表',
  `tag_dict_id` int NOT NULL COMMENT '关联用户标签字典表',
  `obtain_time` datetime NOT NULL COMMENT '标签获取时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '关联记录创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '关联记录更新时间',
  PRIMARY KEY (`user_tag_id`),
  UNIQUE KEY `uk_user_tag_dict` (`user_id`, `tag_dict_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_tag_dict_id` (`tag_dict_id`),
  CONSTRAINT `fk_user_tag_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_user_tag_dict` FOREIGN KEY (`tag_dict_id`) REFERENCES `user_tag_dict` (`tag_dict_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户标签表（存储用户与标签的关联关系）';

-- 用户计数表
CREATE TABLE `user_count_table` (
  `count_id` bigint NOT NULL AUTO_INCREMENT COMMENT '计数唯一标识',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `collect_count` int NOT NULL DEFAULT 0 COMMENT '收藏数',
  `browsing_count` int NOT NULL DEFAULT 0 COMMENT '浏览数',
  `planning_count` int NOT NULL DEFAULT 0 COMMENT '路线规划数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`count_id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_user_count_table` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户计数表，用于给用户发放标签';

-- 创建城市表（city）
CREATE TABLE `city` (
  `city_id` int NOT NULL AUTO_INCREMENT COMMENT '城市唯一标识',
  `city_name` varchar(50) NOT NULL COMMENT '城市名称（不可重复）',
  `country` varchar(50) NOT NULL COMMENT '所属国家',
  `city_url` varchar(255) NOT NULL COMMENT '城市图片',
  `average_temperature` decimal(3,1) DEFAULT NULL COMMENT '平均气温（单位：℃）',
  `attraction_count` int NOT NULL DEFAULT 0 COMMENT '城市内景点数量',
  `popularity` int NOT NULL DEFAULT 0 COMMENT '热度值（热门城市排序依据）',
  `description` text DEFAULT NULL COMMENT '城市简介',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '展示排序序号',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（1=启用，0=禁用）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`city_id`),
  UNIQUE KEY `uk_city_name` (`city_name`),
  KEY `idx_popularity` (`popularity`),
  KEY `idx_status` (`status`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='城市表';

-- 创建景点标签表（attraction_tag）
CREATE TABLE `attraction_tag` (
  `tag_id` int NOT NULL AUTO_INCREMENT COMMENT '标签唯一标识',
  `tag_name` varchar(50) NOT NULL COMMENT '标签名称（不可重复）',
  `description` varchar(255) DEFAULT NULL COMMENT '标签描述',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '展示排序序号',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（1=启用，0=禁用）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`tag_id`),
  UNIQUE KEY `uk_tag_name` (`tag_name`),
  KEY `idx_status` (`status`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='景点标签表（与类型区分，用于推荐和筛选）';

-- 创建管理员表（admin）
CREATE TABLE `admin` (
  `admin_id` bigint NOT NULL AUTO_INCREMENT COMMENT '管理员唯一标识（企业级管理端用户核心ID）',
  `username` varchar(50) NOT NULL COMMENT '管理员用户名（管理端登录凭证）',
  `password` varchar(100) NOT NULL COMMENT '加密密码（PBKDF2算法加盐存储）',
  `password_salt` varchar(100) NOT NULL COMMENT '密码盐值（随机字符串）',
  `pbkdf2_iterations` int NOT NULL DEFAULT 10000 COMMENT 'PBKDF2迭代次数',
  `full_name` varchar(50) DEFAULT NULL COMMENT '管理员真实姓名',
  `phone` varchar(20) DEFAULT NULL COMMENT '管理员手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '管理员邮箱',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '账号状态（1=正常，0=禁用）',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` varchar(50) DEFAULT NULL COMMENT '最后登录IP地址',
  `login_count` int NOT NULL DEFAULT 0 COMMENT '累计登录次数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '账号创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '账号信息更新时间', 
  PRIMARY KEY (`admin_id`),
  UNIQUE KEY `uk_admin_username` (`username`),
  UNIQUE KEY `uk_admin_phone` (`phone`),
  UNIQUE KEY `uk_admin_email` (`email`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表（企业级管理端用户）';

-- 创建景点表（attraction）
CREATE TABLE `attraction` (
  `attraction_id` bigint NOT NULL AUTO_INCREMENT COMMENT '景点唯一标识',
  `name` varchar(100) NOT NULL COMMENT '景点名称（不可重复）',
  `subtitle` varchar(200) DEFAULT NULL COMMENT '景点副标题',
  `type_id` int NOT NULL COMMENT '关联景点类型表',
  `city_id` int NOT NULL COMMENT '关联城市表',
  `address` varchar(255) NOT NULL COMMENT '详细地址',
  `latitude` decimal(10,8) DEFAULT NULL COMMENT '纬度（范围：-90~90）',
  `longitude` decimal(11,8) DEFAULT NULL COMMENT '经度（范围：-180~180）',
  `main_image_url` varchar(255) DEFAULT NULL COMMENT '主图URL',
  `multi_image_urls` text DEFAULT NULL COMMENT '多图URL（JSON格式）',
  `average_rating` decimal(3,2) NOT NULL DEFAULT 0.00 COMMENT '平均评分（0.00~5.00）',
  `rating_count` int NOT NULL DEFAULT 0 COMMENT '评分人数',
  `browse_count` int NOT NULL DEFAULT 0 COMMENT '浏览量',
  `favorite_count` int NOT NULL DEFAULT 0 COMMENT '收藏量（冗余字段）',
  `popularity` int NOT NULL DEFAULT 0 COMMENT '人气指数（综合排序依据）',
  `estimated_play_time` int DEFAULT NULL COMMENT '建议游览时间（单位：分钟）',
  `ticket_price` decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '门票价格（单位：元）',
  `ticket_description` varchar(255) DEFAULT NULL COMMENT '门票说明',
  `opening_hours` varchar(100) DEFAULT NULL COMMENT '开放时间',
  `best_season` varchar(100) DEFAULT NULL COMMENT '最佳观光季节',
  `historical_context` text DEFAULT NULL COMMENT '历史底蕴',
  `safety_tips` varchar(255) DEFAULT NULL COMMENT '安全提示',
  `official_website` varchar(255) DEFAULT NULL COMMENT '官方网站',
  `nearby_food` text DEFAULT NULL COMMENT '附近美食（JSON格式）',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '景点状态（1=正常，0=下架）',
  `audit_status` tinyint NOT NULL DEFAULT 2 COMMENT '审核状态（1=待审核，2=已通过，3=已驳回）',
  `creator_id` bigint DEFAULT NULL COMMENT '创建人ID（关联管理员表）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`attraction_id`),
  UNIQUE KEY `uk_name` (`name`),
  KEY `idx_type_id` (`type_id`),
  KEY `idx_city_id` (`city_id`),
  KEY `idx_average_rating` (`average_rating`),
  KEY `idx_browse_count` (`browse_count`),
  KEY `idx_popularity` (`popularity`),
  KEY `idx_ticket_price` (`ticket_price`),
  KEY `idx_status_audit` (`status`, `audit_status`),
  KEY `idx_coordinate` (`latitude`, `longitude`),
  CONSTRAINT `fk_attraction_type` FOREIGN KEY (`type_id`) REFERENCES `attraction_type` (`type_id`) ON UPDATE CASCADE,
  CONSTRAINT `fk_attraction_city` FOREIGN KEY (`city_id`) REFERENCES `city` (`city_id`) ON UPDATE CASCADE,
  CONSTRAINT `fk_attraction_creator` FOREIGN KEY (`creator_id`) REFERENCES `admin` (`admin_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='景点表';

-- 轮播图表
CREATE TABLE `slideshow` (
  `slideshow_id` int NOT NULL AUTO_INCREMENT COMMENT '轮播图唯一标识',
  `title` varchar(100) NOT NULL COMMENT '轮播图标题',
  `subtitle` varchar(200) DEFAULT NULL COMMENT '副标题/描述',
  `image_url` varchar(500) NOT NULL COMMENT '轮播图图片URL',
  `attraction_id` bigint DEFAULT NULL COMMENT '关联的景点ID（当link_type为attraction时使用）',
  `display_order` int DEFAULT 0 COMMENT '显示顺序（数字越小越靠前）',
  `status` tinyint DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `start_time` datetime DEFAULT NULL COMMENT '开始展示时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束展示时间',
  `click_count` int DEFAULT 0 COMMENT '点击次数',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`slideshow_id`),
  KEY `idx_status` (`status`),
  KEY `idx_display_order` (`display_order`),
  KEY `idx_attraction_id` (`attraction_id`),
  KEY `idx_time_range` (`start_time`, `end_time`),
  CONSTRAINT `fk_slideshow_attraction` FOREIGN KEY (`attraction_id`) REFERENCES `attraction` (`attraction_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='轮播图表';

-- 创建景点-标签关联表（attraction_tag_relation）
CREATE TABLE `attraction_tag_relation` (
  `relation_id` bigint NOT NULL AUTO_INCREMENT COMMENT '景点-标签关联记录唯一标识',
  `attraction_id` bigint NOT NULL COMMENT '关联景点表',
  `tag_id` int NOT NULL COMMENT '关联景点标签表',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '关联记录创建时间',
  PRIMARY KEY (`relation_id`),
  KEY `idx_attraction_id` (`attraction_id`),
  KEY `idx_tag_id` (`tag_id`),
  CONSTRAINT `fk_relation_attraction` FOREIGN KEY (`attraction_id`) REFERENCES `attraction` (`attraction_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_relation_tag` FOREIGN KEY (`tag_id`) REFERENCES `attraction_tag` (`tag_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='景点-标签关联表（实现多对多关系）';

-- 创建用户浏览记录表（user_browse_record）
CREATE TABLE `user_browse_record` (
  `browse_record_id` bigint NOT NULL AUTO_INCREMENT COMMENT '浏览记录唯一标识',
  `user_id` bigint NOT NULL COMMENT '关联用户账号表',
  `attraction_id` bigint NOT NULL COMMENT '关联景点表',
  `browse_duration` int NOT NULL DEFAULT 0 COMMENT '浏览次数',
  `browse_time` datetime NOT NULL COMMENT '浏览时间',
  `device_info` varchar(100) DEFAULT NULL COMMENT '浏览设备',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`browse_record_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_attraction_id` (`attraction_id`),
  KEY `idx_browse_time` (`browse_time`),
  CONSTRAINT `fk_browse_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_browse_attraction` FOREIGN KEY (`attraction_id`) REFERENCES `attraction` (`attraction_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户浏览记录表';

-- 创建用户收藏记录表（user_collection）
CREATE TABLE `user_collection` (
  `collection_id` bigint NOT NULL AUTO_INCREMENT COMMENT '收藏记录唯一标识',
  `user_id` bigint NOT NULL COMMENT '关联用户账号表',
  `attraction_id` bigint NOT NULL COMMENT '关联景点表',
  `collection_time` datetime NOT NULL COMMENT '收藏时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除标记（0=未删除，1=已删除）',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`collection_id`),
  UNIQUE KEY `uk_user_attraction` (`user_id`, `attraction_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_attraction_id` (`attraction_id`),
  KEY `idx_is_deleted` (`is_deleted`),
  CONSTRAINT `fk_collection_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_collection_attraction` FOREIGN KEY (`attraction_id`) REFERENCES `attraction` (`attraction_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏记录表';

-- 创建旅游路线表（travel_route）
CREATE TABLE `travel_route` (
  `travel_route_id` bigint NOT NULL AUTO_INCREMENT COMMENT '路线唯一标识',
  `user_id` bigint NOT NULL COMMENT '关联用户账号表（创建者）',
  `route_name` varchar(100) NOT NULL COMMENT '路线名称（如“北京3日游”）',
  `start_date` date DEFAULT NULL COMMENT '出发日期',
  `end_date` date DEFAULT NULL COMMENT '结束日期',
  `total_duration` int DEFAULT NULL COMMENT '总时长（单位：天）',
  `total_distance` decimal(10,2) DEFAULT NULL COMMENT '总距离（单位：公里）',
  `total_cost` decimal(12,2) DEFAULT NULL COMMENT '路线总预计花费（单位：元）',
  `algorithm_params` text DEFAULT NULL COMMENT '算法输入参数（JSON格式）',
  `weather_summary` varchar(255) DEFAULT NULL COMMENT '路线整体气候总结',
  `is_favorite` tinyint NOT NULL DEFAULT 0 COMMENT '是否收藏（0=否，1=是）',
  `view_count` int NOT NULL DEFAULT 0 COMMENT '查看次数',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除标记（0=未删除，1=已删除）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`travel_route_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_is_deleted` (`is_deleted`),
  KEY `idx_view_count` (`view_count`),
  CONSTRAINT `fk_route_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='旅游路线表';

-- 创建路线-景点关联表（route_attraction_relation）
CREATE TABLE `route_attraction_relation` (
  `relation_id` bigint NOT NULL AUTO_INCREMENT COMMENT '路线-景点关联记录唯一标识',
  `travel_route_id` bigint NOT NULL COMMENT '关联旅游路线表',
  `attraction_id` bigint NOT NULL COMMENT '关联景点表',
  `sequence` int NOT NULL COMMENT '景点顺序（前端渲染顺序依据）',
  `stay_days` int NOT NULL DEFAULT 1 COMMENT '该景点停留天数',
  `single_attraction_cost` decimal(10,2) DEFAULT NULL COMMENT '该景点单独预计花费（单位：元）',
  `daily_weather` varchar(50) DEFAULT NULL COMMENT '停留当日气候',
  `traffic_cost` decimal(10,2) DEFAULT NULL COMMENT '前往该景点的路费（单位：元）',
  `route_tips` varchar(255) DEFAULT NULL COMMENT '该景点行程小贴士',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`relation_id`),
  KEY `idx_route_id` (`travel_route_id`),
  KEY `idx_attraction_id` (`attraction_id`),
  KEY `idx_sequence` (`sequence`),
  CONSTRAINT `fk_route_relation_route` FOREIGN KEY (`travel_route_id`) REFERENCES `travel_route` (`travel_route_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_route_relation_attraction` FOREIGN KEY (`attraction_id`) REFERENCES `attraction` (`attraction_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='路线-景点关联表（存储路线中景点的详细规划）';

-- 创建景点历史人流数据表（attraction_historical_flow）
CREATE TABLE `attraction_historical_flow` (
  `historical_flow_id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录唯一标识',
  `attraction_id` bigint NOT NULL COMMENT '关联景点表',
  `date` date NOT NULL COMMENT '日期（精确到天）',
  `flow_count` int NOT NULL COMMENT '当日人流数量',
  `weather_condition` varchar(50) DEFAULT NULL COMMENT '当日天气',
  `holiday_flag` tinyint NOT NULL DEFAULT 0 COMMENT '是否节假日（0=否，1=是）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`historical_flow_id`),
  UNIQUE KEY `uk_attraction_date` (`attraction_id`, `date`),
  KEY `idx_attraction_id` (`attraction_id`),
  KEY `idx_date` (`date`),
  KEY `idx_holiday_flag` (`holiday_flag`),
  CONSTRAINT `fk_flow_attraction` FOREIGN KEY (`attraction_id`) REFERENCES `attraction` (`attraction_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='景点历史人流数据表（供LSTM模型使用）';

-- 创建管理员角色表（admin_role）
CREATE TABLE `admin_role` (
  `role_id` int NOT NULL AUTO_INCREMENT COMMENT '角色唯一标识（企业级权限分级核心）',
  `role_name` varchar(50) NOT NULL COMMENT '角色名称（不可重复）',
  `role_desc` varchar(255) DEFAULT NULL COMMENT '角色描述',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '角色状态（1=启用，0=禁用）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '角色创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '角色更新时间',
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `uk_role_name` (`role_name`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员角色表';

-- 创建管理员权限表（admin_permission）
CREATE TABLE `admin_permission` (
  `permission_id` int NOT NULL AUTO_INCREMENT COMMENT '权限唯一标识（精细化权限控制核心）',
  `permission_code` varchar(100) NOT NULL COMMENT '权限编码（代码中统一引用）',
  `permission_name` varchar(100) NOT NULL COMMENT '权限名称',
  `resource_type` varchar(50) NOT NULL COMMENT '资源类型（如“用户管理”“景点管理”）',
  `resource_path` varchar(255) DEFAULT NULL COMMENT '关联接口路径（用于接口权限拦截）',
  `is_sensitive` tinyint NOT NULL DEFAULT 0 COMMENT '是否敏感权限（1=是，0=否）',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '权限排序序号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '权限创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '权限更新时间',
  PRIMARY KEY (`permission_id`),
  UNIQUE KEY `uk_permission_code` (`permission_code`),
  KEY `idx_resource_type` (`resource_type`),
  KEY `idx_is_sensitive` (`is_sensitive`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员权限表';

-- 创建角色-权限关联表（admin_role_permission）
CREATE TABLE `admin_role_permission` (
  `role_permission_id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色-权限关联记录唯一标识',
  `role_id` int NOT NULL COMMENT '关联管理员角色表',
  `permission_id` int NOT NULL COMMENT '关联管理员权限表',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '关联记录创建时间',
  PRIMARY KEY (`role_permission_id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_permission_id` (`permission_id`),
  CONSTRAINT `fk_role_permission_role` FOREIGN KEY (`role_id`) REFERENCES `admin_role` (`role_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_role_permission_permission` FOREIGN KEY (`permission_id`) REFERENCES `admin_permission` (`permission_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色-权限关联表（给角色分配权限）';

-- 创建管理员-角色关联表（admin_role_relation）
CREATE TABLE `admin_role_relation` (
  `admin_role_id` bigint NOT NULL AUTO_INCREMENT COMMENT '管理员-角色关联记录唯一标识',
  `admin_id` bigint NOT NULL COMMENT '关联管理员表',
  `role_id` int NOT NULL COMMENT '关联管理员角色表',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '关联记录创建时间',
  PRIMARY KEY (`admin_role_id`),
  KEY `idx_admin_id` (`admin_id`),
  KEY `idx_role_id` (`role_id`),
  CONSTRAINT `fk_admin_role_admin` FOREIGN KEY (`admin_id`) REFERENCES `admin` (`admin_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_admin_role_role` FOREIGN KEY (`role_id`) REFERENCES `admin_role` (`role_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员-角色关联表（给管理员分配角色）';

-- 创建系统操作日志表（operation_log）
CREATE TABLE `operation_log` (
  `operation_log_id` bigint NOT NULL AUTO_INCREMENT COMMENT '操作日志唯一标识（安全审计核心）',
  `admin_id` bigint NOT NULL COMMENT '关联管理员表（操作人）',
  `operation_type` varchar(50) NOT NULL COMMENT '操作类型（如“景点新增”“类型编辑”）',
  `operation_object` varchar(50) NOT NULL COMMENT '操作对象（如“景点”“类型”“标签”）',
  `object_id` bigint NOT NULL COMMENT '操作对象ID（如景点ID=1001）',
  `operation_content` text NOT NULL COMMENT '操作内容（详细描述）',
  `operation_ip` varchar(50) DEFAULT NULL COMMENT '操作IP地址',
  `operation_time` datetime NOT NULL COMMENT '操作时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '日志创建时间',
  PRIMARY KEY (`operation_log_id`),
  KEY `idx_admin_id` (`admin_id`),
  KEY `idx_operation_time` (`operation_time`),
  KEY `idx_operation_object` (`operation_object`),
  CONSTRAINT `fk_operation_log_admin` FOREIGN KEY (`admin_id`) REFERENCES `admin` (`admin_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统操作日志表（管理端操作审计）';