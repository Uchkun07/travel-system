CREATE TABLE `user` (
  `user_id` int NOT NULL AUTO_INCREMENT COMMENT '用户唯一标识',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `email` varchar(100) NOT NULL COMMENT '电子邮箱',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号码',
  `full_name` varchar(100) DEFAULT NULL COMMENT '真实姓名',
  `gender` tinyint DEFAULT 0 COMMENT '性别(0-未知,1-男,2-女)',
  `birthday` date DEFAULT NULL COMMENT '出生日期',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `address` varchar(255) DEFAULT NULL COMMENT '常住地址',
  `register_time` datetime NOT NULL COMMENT '注册时间',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态(0-禁用,1-正常)',
  `preference_type` varchar(255) DEFAULT NULL COMMENT '偏好旅游类型',
  `preference_destination` varchar(255) DEFAULT NULL COMMENT '偏好目的地',
  `budget_range` varchar(50) DEFAULT NULL COMMENT '预算范围',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`),
  UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';


INSERT INTO `user` (`username`, `password`, `email`, `phone`, `full_name`, `gender`, `birthday`, `avatar`, `address`, `register_time`, `last_login_time`, `status`, `preference_type`, `preference_destination`, `budget_range`) VALUES
('traveler_john', 'pbkdf2$100000$a1b2c3d4e5f6a7b8$3a7f9d2c4e6b8a0d1e2f3a4b5c6d7e8f9a0b1c2d3e4f5a6b7c8d9e0f1a2b3c4', 'john.doe@example.com', '13800138001', 'John Doe', 1, '1990-05-15', 'https://picsum.photos/id/1012/200', '北京市朝阳区建国路88号', '2023-01-10 09:30:00', '2023-06-15 14:20:00', 1, '自然风光,徒步旅行', '瑞士,新西兰,西藏', '5000-10000'),

('wanderlust_amy', 'pbkdf2$100000$b2c3d4e5f6a7b8c9$4b8g0e3d5f7a9b1c2d3e4f5a6b7c8d9e0f1a2b3c4d5e6f7a8b9c0d1e2f3a4b5', 'amy.smith@example.com', '13900139002', 'Amy Smith', 2, '1995-08-22', 'https://picsum.photos/id/1027/200', '上海市静安区南京西路1266号', '2023-02-15 10:15:00', '2023-06-20 09:45:00', 1, '文化古迹,美食之旅', '意大利,日本,西安', '3000-8000'),

('backpacker_li', 'pbkdf2$100000$c3d4e5f6a7b8c9d0$5c9h1f4e6g8b0c2d3e4f5a6b7c8d9e0f1a2b3c4d5e6f7a8b9c0d1e2f3a4b5c6', 'li.ming@example.com', '13700137003', '李明', 1, '1998-03-10', 'https://picsum.photos/id/1074/200', '广州市天河区天河路385号', '2023-03-05 16:40:00', '2023-06-18 16:30:00', 1, '背包旅行,探险', '尼泊尔,云南,四川', '1000-5000'),

('luxury_traveler', 'pbkdf2$100000$d4e5f6a7b8c9d0e1$6d0i2g5f7h9c1d3e4f5a6b7c8d9e0f1a2b3c4d5e6f7a8b9c0d1e2f3a4b5c6d7', 'sophia@example.com', '13600136004', 'Sophia Wang', 2, '1985-11-30', 'https://picsum.photos/id/1062/200', '深圳市福田区深南大道1003号', '2023-01-20 11:25:00', '2023-06-22 11:10:00', 1, '奢华度假,海岛', '马尔代夫,巴厘岛,三亚', '10000-20000'),

('city_explorer', 'pbkdf2$100000$e5f6a7b8c9d0e1f2$7e1j3h6g8i0d2e4f5a6b7c8d9e0f1a2b3c4d5e6f7a8b9c0d1e2f3a4b5c6d7e8', 'david@example.com', '13500135005', 'David Chen', 1, '2000-07-05', 'https://picsum.photos/id/1025/200', '成都市锦江区红星路三段1号', '2023-04-12 15:30:00', '2023-06-19 10:50:00', 1, '城市观光,购物', '巴黎,东京,香港', '5000-15000'),

('historical_anna', 'pbkdf2$100000$f6a7b8c9d0e1f2a3$8f2k4i7h9j1e3f5a6b7c8d9e0f1a2b3c4d5e6f7a8b9c0d1e2f3a4b5c6d7e8f9', 'anna@example.com', '13400134006', 'Anna Liu', 2, '1993-09-18', 'https://picsum.photos/id/1066/200', '西安市雁塔区大雁塔南广场', '2023-02-28 09:10:00', '2023-06-10 15:40:00', 1, '历史古迹,文化体验', '埃及,希腊,北京', '3000-7000'),

('nature_lover', 'pbkdf2$100000$a7b8c9d0e1f2a3b4$9a3l5j8i0k2f4g6a7b8c9d0e1f2a3b4c5d6e7f8a9b0c1d2e3f4a5b6c7d8e9f0', 'chris@example.com', '13300133007', 'Chris Zhang', 1, '1988-04-22', 'https://picsum.photos/id/1035/200', '杭州市西湖区龙井路88号', '2023-01-05 14:50:00', '2023-06-05 08:30:00', 1, '国家公园,生态旅游', '加拿大,挪威,青海', '8000-15000'),

('food_tourist', 'pbkdf2$100000$b8c9d0e1f2a3b4c5$0b4m6k9j1l3g5h7b8c9d0e1f2a3b4c5d6e7f8a9b0c1d2e3f4a5b6c7d8e9f0a1', 'maria@example.com', '13200132008', 'Maria Lee', 2, '1996-12-03', 'https://picsum.photos/id/1083/200', '重庆市渝中区解放碑八一路', '2023-03-20 11:45:00', '2023-06-21 12:20:00', 1, '美食之旅,夜市', '泰国,韩国,成都', '2000-6000'),

('road_trip_kevin', 'pbkdf2$100000$c9d0e1f2a3b4c5d6$1c5n7l0k2m4h6i8c9d0e1f2a3b4c5d6e7f8a9b0c1d2e3f4a5b6c7d8e9f0a1b2', 'kevin@example.com', '13100131009', 'Kevin Wu', 1, '1991-06-14', 'https://picsum.photos/id/1076/200', '武汉市江汉区解放大道690号', '2023-04-02 16:20:00', NULL, 1, '公路旅行,自驾游', '美国,澳大利亚,新疆', '6000-12000'),

('photographer_zoe', 'pbkdf2$100000$d0e1f2a3b4c5d6e7$2d6o8m1l3n5i7j9d0e1f2a3b4c5d6e7f8a9b0c1d2e3f4a5b6c7d8e9f0a1b2c3', 'zoe@example.com', '13000130010', 'Zoe Zhao', 2, '1994-02-27', 'https://picsum.photos/id/1084/200', '南京市玄武区中山陵3号', '2023-05-18 10:30:00', '2023-06-17 17:15:00', 0, '摄影之旅,自然风光', '冰岛,新西兰,甘肃', '4000-9000');
