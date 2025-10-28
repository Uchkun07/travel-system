-- 轮播图表
CREATE TABLE `slideshow` (
  `slideshow_id` int NOT NULL AUTO_INCREMENT COMMENT '轮播图唯一标识',
  `title` varchar(100) NOT NULL COMMENT '轮播图标题',
  `subtitle` varchar(200) DEFAULT NULL COMMENT '副标题/描述',
  `image_url` varchar(500) NOT NULL COMMENT '轮播图图片URL',
  `attraction_id` int DEFAULT NULL COMMENT '关联的景点ID（当link_type为attraction时使用）',
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

-- 插入轮播图示例数据
INSERT INTO `slideshow` (`title`, `subtitle`, `image_url`, `attraction_id`, `display_order`, `status`, `start_time`, `end_time`) VALUES
('故宫博物院', '探索紫禁城的千年历史', 'https://example.com/images/forbidden-city.jpg', 1, 1, 1, '2024-01-01 00:00:00', '2024-12-31 23:59:59'),
('长城之旅', '登临万里长城，感受壮丽景观', 'https://example.com/images/great-wall.jpg', 2, 2, 1, '2024-01-01 00:00:00', '2024-12-31 23:59:59'),
('西湖美景', '人间天堂，醉美西湖', 'https://example.com/images/west-lake.jpg', 3, 3, 1, '2024-03-01 00:00:00', '2024-10-31 23:59:59'),
('黄山云海', '奇松怪石，云海奇观', 'https://example.com/images/huangshan.jpg', 4, 4, 1, '2024-04-01 00:00:00', '2024-11-30 23:59:59'),
('九寨沟风光', '人间仙境，五彩斑斓', 'https://example.com/images/jiuzhaigou.jpg', 5, 5, 1, '2024-05-01 00:00:00', '2024-10-31 23:59:59');
