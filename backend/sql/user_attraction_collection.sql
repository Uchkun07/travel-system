CREATE TABLE `user_attraction_collection` (
	`collection_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '收藏记录唯一ID（对应你的“收藏id”）',
	`user_id` INT NOT NULL COMMENT '用户ID（关联用户表user的user_id）',
	`attraction_id` INT NOT NULL COMMENT '单个收藏景点ID（关联景点表attraction的attraction_id）',
	`collect_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间（新增，支撑排序需求）',
	`is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记（新增，支撑取消收藏需求）',
	PRIMARY KEY (`collection_id`),
	UNIQUE KEY `uk_user_attraction` (`user_id`, `attraction_id`),
	KEY `idx_user_id` (`user_id`),
	KEY `idx_attraction_id` (`attraction_id`),
	CONSTRAINT `fk_collection_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE,
	CONSTRAINT `fk_collection_attraction` FOREIGN KEY (`attraction_id`) REFERENCES `attraction` (`attraction_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户景点收藏表';