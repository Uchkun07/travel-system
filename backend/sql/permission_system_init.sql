-- ========================================
-- 权限管理系统 - 完整数据库初始化脚本
-- 包含: 所有权限、超级管理员角色、超级管理员用户
-- ========================================

-- ----------------------------------------
-- 1. 插入所有系统权限数据 (46个权限)
-- ----------------------------------------

INSERT INTO `admin_permission` (`permission_code`, `permission_name`, `resource_type`, `resource_path`, `is_sensitive`, `sort_order`) VALUES
-- 管理员管理 (4个)
('ADMIN:CREATE', '创建管理员', '管理员管理', '/admin/create', 1, 5),
('ADMIN:DELETE', '删除管理员', '管理员管理', '/admin/delete/**', 1, 6),
('ADMIN:UPDATE', '修改管理员', '管理员管理', '/admin/update', 1, 7),
('ADMIN:VIEW', '查看管理员', '管理员管理', '/admin/list', 0, 8),

-- 权限管理 (4个)
('PERMISSION:CREATE', '创建权限', '权限管理', '/admin/permission/create', 1, 10),
('PERMISSION:DELETE', '删除权限', '权限管理', '/admin/permission/delete/**', 1, 20),
('PERMISSION:UPDATE', '修改权限', '权限管理', '/admin/permission/update', 1, 30),
('PERMISSION:VIEW', '查看权限', '权限管理', '/admin/permission/**', 0, 40),

-- 角色管理 (4个)
('ROLE:CREATE', '创建角色', '角色管理', '/admin/role/create', 1, 50),
('ROLE:DELETE', '删除角色', '角色管理', '/admin/role/delete/**', 1, 60),
('ROLE:UPDATE', '修改角色', '角色管理', '/admin/role/update', 1, 70),
('ROLE:VIEW', '查看角色', '角色管理', '/admin/role/**', 0, 80),

-- 角色权限绑定 (3个)
('ROLE_PERMISSION:BIND', '角色绑定权限', '角色权限', '/admin/role-permission/bind', 1, 90),
('ROLE_PERMISSION:UNBIND', '角色解绑权限', '角色权限', '/admin/role-permission/unbind', 1, 100),
('ROLE_PERMISSION:VIEW', '查看角色权限', '角色权限', '/admin/role-permission/list/**', 0, 110),

-- 管理员角色绑定 (3个)
('ADMIN_ROLE:BIND', '管理员绑定角色', '管理员角色', '/admin/admin-role/bind', 1, 120),
('ADMIN_ROLE:UNBIND', '管理员解绑角色', '管理员角色', '/admin/admin-role/unbind', 1, 130),
('ADMIN_ROLE:VIEW', '查看管理员角色', '管理员角色', '/admin/admin-role/list/**', 0, 140),

-- 景点类型管理 (4个)
('ATTRACTION_TYPE:CREATE', '创建景点类型', '景点管理', '/admin/attraction-type/create', 0, 150),
('ATTRACTION_TYPE:DELETE', '删除景点类型', '景点管理', '/admin/attraction-type/delete/**', 0, 160),
('ATTRACTION_TYPE:UPDATE', '修改景点类型', '景点管理', '/admin/attraction-type/update', 0, 170),
('ATTRACTION_TYPE:VIEW', '查看景点类型', '景点管理', '/admin/attraction-type/**', 0, 180),

-- 景点管理 (5个)
('ATTRACTION:CREATE', '创建景点', '景点管理', '/admin/attraction/create', 0, 191),
('ATTRACTION:DELETE', '删除景点', '景点管理', '/admin/attraction/delete/**', 0, 192),
('ATTRACTION:UPDATE', '修改景点', '景点管理', '/admin/attraction/update', 0, 193),
('ATTRACTION:LIST', '查看景点列表', '景点管理', '/admin/attraction/list', 0, 194),
('ATTRACTION:DETAIL', '查看景点详情', '景点管理', '/admin/attraction/detail/**', 0, 195),

-- 城市管理 (4个)
('CITY:CREATE', '创建城市', '城市管理', '/admin/city/create', 0, 200),
('CITY:DELETE', '删除城市', '城市管理', '/admin/city/delete/**', 0, 210),
('CITY:UPDATE', '修改城市', '城市管理', '/admin/city/update', 0, 220),
('CITY:VIEW', '查看城市', '城市管理', '/admin/city/**', 0, 230),

-- 轮播图管理 (4个)
('SLIDESHOW:CREATE', '创建轮播图', '轮播图管理', '/admin/slideshow/create', 0, 240),
('SLIDESHOW:DELETE', '删除轮播图', '轮播图管理', '/admin/slideshow/delete/**', 0, 250),
('SLIDESHOW:UPDATE', '修改轮播图', '轮播图管理', '/admin/slideshow/update', 0, 260),
('SLIDESHOW:VIEW', '查看轮播图', '轮播图管理', '/admin/slideshow/**', 0, 270),

-- 景点标签管理 (4个)
('ATTRACTION_TAG:CREATE', '创建景点标签', '标签管理', '/admin/attraction-tag/create', 0, 280),
('ATTRACTION_TAG:DELETE', '删除景点标签', '标签管理', '/admin/attraction-tag/delete/**', 0, 290),
('ATTRACTION_TAG:UPDATE', '修改景点标签', '标签管理', '/admin/attraction-tag/update', 0, 300),
('ATTRACTION_TAG:VIEW', '查看景点标签', '标签管理', '/admin/attraction-tag/**', 0, 310),

-- 操作日志管理 (2个)
('OPERATION_LOG:DELETE', '删除操作日志', '日志管理', '/admin/operation-log/delete/**', 1, 320),
('OPERATION_LOG:VIEW', '查看操作日志', '日志管理', '/admin/operation-log/**', 0, 330),

-- 景点-标签关联管理 (3个)
('ATTRACTION_TAG_RELATION:BIND', '绑定景点标签', '标签关联管理', '/admin/attraction-tag-relation/bind', 0, 340),
('ATTRACTION_TAG_RELATION:UNBIND', '解绑景点标签', '标签关联管理', '/admin/attraction-tag-relation/unbind', 0, 350),
('ATTRACTION_TAG_RELATION:VIEW', '查看景点标签关联', '标签关联管理', '/admin/attraction-tag-relation/list/**', 0, 360),

-- 系统管理 (1个)
('SYSTEM:MANAGE', '系统管理', '系统管理', '/admin/**', 1, 1000)
ON DUPLICATE KEY UPDATE 
    `permission_name` = VALUES(`permission_name`),
    `resource_type` = VALUES(`resource_type`),
    `resource_path` = VALUES(`resource_path`),
    `is_sensitive` = VALUES(`is_sensitive`),
    `sort_order` = VALUES(`sort_order`);

-- ----------------------------------------
-- 2. 创建超级管理员角色
-- ----------------------------------------

INSERT INTO `admin_role` (`role_id`, `role_name`, `role_desc`, `status`) VALUES
(1, '超级管理员', '拥有系统所有权限,可以管理所有模块', 1)
ON DUPLICATE KEY UPDATE 
    `role_name` = VALUES(`role_name`),
    `role_desc` = VALUES(`role_desc`),
    `status` = VALUES(`status`);

-- ----------------------------------------
-- 3. 为超级管理员角色分配所有权限
-- ----------------------------------------

-- 删除旧的权限分配
DELETE FROM `admin_role_permission` WHERE `role_id` = 1;

-- 为超级管理员角色分配所有权限
INSERT INTO `admin_role_permission` (`role_id`, `permission_id`)
SELECT 1, `permission_id` FROM `admin_permission`;

-- ----------------------------------------
-- 4. 创建超级管理员用户
-- ----------------------------------------

INSERT INTO `admin` (
    `admin_id`, 
    `username`, 
    `password`, 
    `password_salt`, 
    `pbkdf2_iterations`, 
    `login_count`, 
    `full_name`, 
    `phone`, 
    `email`, 
    `status`, 
    `last_login_time`
) VALUES (
    1, 
    'superadmin', 
    'f841a11680110680e7a603f391792f356655ab7e6e1d6ca488a4da5fefa2418c', 
    'abc123salt', 
    10000, 
    0, 
    '张超级', 
    '13800000001', 
    'superadmin@travel.com', 
    1, 
    NOW()
)
ON DUPLICATE KEY UPDATE 
    `username` = VALUES(`username`),
    `password` = VALUES(`password`),
    `password_salt` = VALUES(`password_salt`),
    `pbkdf2_iterations` = VALUES(`pbkdf2_iterations`),
    `login_count` = VALUES(`login_count`),
    `full_name` = VALUES(`full_name`),
    `phone` = VALUES(`phone`),
    `email` = VALUES(`email`),
    `status` = VALUES(`status`);

-- ----------------------------------------
-- 5. 为超级管理员用户分配超级管理员角色
-- ----------------------------------------

INSERT INTO `admin_role_relation` (`admin_id`, `role_id`) VALUES (1, 1)
ON DUPLICATE KEY UPDATE `role_id` = VALUES(`role_id`);

-- ----------------------------------------
-- 6. 查询验证
-- ----------------------------------------

-- 查询所有权限
SELECT '========== 所有权限 ==========' AS info;
SELECT `permission_code`, `permission_name`, `resource_type`, `is_sensitive` 
FROM `admin_permission` 
ORDER BY `sort_order`;

-- 查询超级管理员角色
SELECT '========== 超级管理员角色 ==========' AS info;
SELECT `role_id`, `role_name`, `role_desc`, `status` 
FROM `admin_role` 
WHERE `role_id` = 1;

-- 查询超级管理员角色的权限数量
SELECT '========== 超级管理员角色权限统计 ==========' AS info;
SELECT 
    ar.`role_name`,
    COUNT(arp.`permission_id`) AS permission_count
FROM `admin_role` ar
LEFT JOIN `admin_role_permission` arp ON ar.`role_id` = arp.`role_id`
WHERE ar.`role_id` = 1
GROUP BY ar.`role_id`, ar.`role_name`;

-- 查询超级管理员用户
SELECT '========== 超级管理员用户 ==========' AS info;
SELECT 
    `admin_id`, 
    `username`, 
    `full_name`, 
    `phone`, 
    `email`, 
    `status`,
    `login_count`
FROM `admin` 
WHERE `admin_id` = 1;

-- 查询超级管理员用户的角色
SELECT '========== 超级管理员用户角色 ==========' AS info;
SELECT 
    a.`username`,
    a.`full_name`,
    GROUP_CONCAT(ar.`role_name` SEPARATOR ', ') AS roles
FROM `admin` a
LEFT JOIN `admin_role_relation` arr ON a.`admin_id` = arr.`admin_id`
LEFT JOIN `admin_role` ar ON arr.`role_id` = ar.`role_id`
WHERE a.`admin_id` = 1
GROUP BY a.`admin_id`, a.`username`, a.`full_name`;

-- 最终统计
SELECT '========== 初始化完成统计 ==========' AS info;
SELECT 
    (SELECT COUNT(*) FROM `admin_permission`) AS total_permissions,
    (SELECT COUNT(*) FROM `admin_role` WHERE `role_id` = 1) AS total_roles,
    (SELECT COUNT(*) FROM `admin` WHERE `admin_id` = 1) AS total_admins,
    (SELECT COUNT(*) FROM `admin_role_permission` WHERE `role_id` = 1) AS role_permissions_count,
    (SELECT COUNT(*) FROM `admin_role_relation` WHERE `admin_id` = 1) AS admin_roles_count;

SELECT '✅ 初始化完成！超级管理员已创建，用户名: superadmin' AS message;
