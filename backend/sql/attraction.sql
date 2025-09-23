CREATE TABLE `attraction` (
  `attraction_id` int NOT NULL AUTO_INCREMENT COMMENT '景点唯一标识',
  `name` varchar(100) NOT NULL COMMENT '景点名称',
  `description` text COMMENT '景点描述',
  `type` varchar(50) DEFAULT NULL COMMENT '景点类型（如自然风光、文化古迹等）',
  `location` varchar(255) NOT NULL COMMENT '景点地址',
  `latitude` decimal(10, 8) DEFAULT NULL COMMENT '纬度（范围：-90到90，小数点后8位）',
  `longitude` decimal(11, 8) DEFAULT NULL COMMENT '经度（范围：-180到180，小数点后8位）',
  `image_url` varchar(255) DEFAULT NULL COMMENT '景点图片URL',
  `average_rating` decimal(3,2) DEFAULT 0 COMMENT '平均评分',
  `view_count` int DEFAULT 0 COMMENT '点击量/浏览量',
  `popularity` int DEFAULT 0 COMMENT '人气指数（综合评分）',
  `estimated_time` int DEFAULT NULL COMMENT '建议游览时间（分钟）',
  `ticket_price` decimal(10,2) DEFAULT 0 COMMENT '门票价格',
  `opening_hours` varchar(100) DEFAULT NULL COMMENT '开放时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`attraction_id`),
  KEY `idx_type` (`type`),
  KEY `idx_popularity` (`popularity`),
  KEY `idx_view_count` (`view_count`),
  KEY `idx_location` (`latitude`, `longitude`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='景点信息表';

-- 插入修正后的示例景点数据（经度使用decimal(11,8)）
INSERT INTO `attraction` (`name`, `description`, `type`, `location`, `latitude`, `longitude`, `image_url`, `average_rating`, `view_count`, `popularity`, `estimated_time`, `ticket_price`, `opening_hours`) VALUES
('故宫博物院', '北京故宫是中国明清两代的皇家宫殿，旧称紫禁城，位于北京中轴线的中心。故宫以三大殿为中心，占地面积约72万平方米，建筑面积约15万平方米，有大小宫殿七十多座，房屋九千余间。', '文化古迹', '北京市东城区景山前街4号', 39.91634500, 116.39715500, 'https://picsum.photos/id/103/800/600', 4.8, 12500, 8900, 240, 60.00, '08:30-17:00'),

('九寨沟风景区', '九寨沟位于四川省阿坝藏族羌族自治州九寨沟县境内，是一条纵深50余千米的山沟谷地，因沟内有树正寨、荷叶寨、则查洼寨等九个藏族村寨坐落在这片高山湖泊群中而得名。', '自然风光', '四川省阿坝藏族羌族自治州九寨沟县', 33.25901600, 103.92377300, 'https://picsum.photos/id/1018/800/600', 4.9, 9800, 9200, 360, 220.00, '07:00-17:00'),

('上海迪士尼乐园', '上海迪士尼乐园是中国内地首座迪士尼主题乐园，拥有米奇大街、奇想花园、梦幻世界、探险岛、宝藏湾和明日世界等主题园区。', '主题公园', '上海市浦东新区川沙新镇黄赵路310号', 31.14434400, 121.65756200, 'https://picsum.photos/id/1020/800/600', 4.7, 15600, 9500, 480, 399.00, '09:00-21:00'),

('西湖风景区', '西湖位于杭州市西部，是中国主要的观赏性淡水湖泊，也是中国首批国家重点风景名胜区。西湖三面环山，面积约6.39平方千米，东西宽约2.8千米，南北长约3.2千米，绕湖一周近15千米。', '自然风光', '浙江省杭州市西湖区龙井路1号', 30.24602600, 120.14322200, 'https://picsum.photos/id/1015/800/600', 4.6, 11200, 7800, 180, 0.00, '全天开放'),

('秦始皇兵马俑博物馆', '兵马俑是古代墓葬雕塑的一个类别，位于今陕西省西安市临潼区秦始皇陵以东1.5千米处的兵马俑坑内，是第一批全国重点文物保护单位，也是第一批中国世界遗产。', '文化古迹', '陕西省西安市临潼区秦陵北路', 34.38444800, 109.27492800, 'https://picsum.photos/id/1019/800/600', 4.8, 8900, 8200, 150, 120.00, '08:30-18:00'),

('张家界国家森林公园', '张家界国家森林公园是中国第一个国家森林公园，以其独特的石英砂岩峰林地貌著称，有"三千奇峰，八百秀水"之美誉，是电影《阿凡达》取景地之一。', '自然风光', '湖南省张家界市武陵源区', 29.35496000, 110.54621100, 'https://picsum.photos/id/1040/800/600', 4.7, 7600, 7100, 300, 228.00, '07:00-18:00'),

('广州长隆旅游度假区', '长隆旅游度假区是综合性主题旅游度假区，拥有长隆欢乐世界、长隆水上乐园、长隆野生动物世界、长隆国际大马戏等主题公园。', '主题公园', '广东省广州市番禺区汉溪大道东299号', 23.00112000, 113.33223700, 'https://picsum.photos/id/1039/800/600', 4.6, 6800, 6500, 420, 350.00, '09:30-20:00'),

('丽江古城', '丽江古城位于云南省丽江市古城区，坐落在丽江坝中部，始建于宋末元初，地处云贵高原，面积为7.279平方公里。丽江古城有着多彩的地方民族习俗和娱乐活动。', '文化古迹', '云南省丽江市古城区', 26.87207000, 100.23431200, 'https://picsum.photos/id/1024/800/600', 4.5, 10500, 7600, 120, 50.00, '全天开放'),

('三亚亚龙湾', '亚龙湾是海南省三亚市东郊的一处优质热带海滨风景区，沙滩平缓开阔，沙粒洁白细软，海水清澈澄莹，海底资源丰富，生长众多原始热带植被，颇具热带海岛风情。', '海滩海岛', '海南省三亚市吉阳区亚龙湾路', 18.23979000, 109.63449100, 'https://picsum.photos/id/1032/800/600', 4.7, 9200, 7800, 240, 0.00, '全天开放'),

('成都大熊猫繁育研究基地', '成都大熊猫繁育研究基地是一个专门从事濒危野生动物研究、繁育、保护教育和教育旅游的非营利性机构，这里生活着众多可爱的大熊猫。', '生态旅游', '四川省成都市成华区熊猫大道1375号', 30.74145500, 104.14720400, 'https://picsum.photos/id/1022/800/600', 4.6, 8400, 6900, 180, 55.00, '07:30-18:00');