-- ========================================
-- 权限管理系统 - 完整数据库初始化脚本
-- ========================================

-- ----------------------------------------
-- 1. 插入系统权限数据
-- ----------------------------------------

INSERT INTO `admin_permission` (`permission_code`, `permission_name`, `resource_type`, `resource_path`, `is_sensitive`, `sort_order`) VALUES
-- 权限管理
('PERMISSION:CREATE', '创建权限', '权限管理', '/admin/permission/create', 1, 10),
('PERMISSION:DELETE', '删除权限', '权限管理', '/admin/permission/delete/**', 1, 20),
('PERMISSION:UPDATE', '修改权限', '权限管理', '/admin/permission/update', 1, 30),
('PERMISSION:VIEW', '查看权限', '权限管理', '/admin/permission/**', 0, 40),

-- 角色管理
('ROLE:CREATE', '创建角色', '角色管理', '/admin/role/create', 1, 50),
('ROLE:DELETE', '删除角色', '角色管理', '/admin/role/delete/**', 1, 60),
('ROLE:UPDATE', '修改角色', '角色管理', '/admin/role/update', 1, 70),
('ROLE:VIEW', '查看角色', '角色管理', '/admin/role/**', 0, 80),

-- 角色权限绑定
('ROLE_PERMISSION:BIND', '角色绑定权限', '角色权限', '/admin/role-permission/bind', 1, 90),
('ROLE_PERMISSION:UNBIND', '角色解绑权限', '角色权限', '/admin/role-permission/unbind', 1, 100),
('ROLE_PERMISSION:VIEW', '查看角色权限', '角色权限', '/admin/role-permission/list/**', 0, 110),

-- 管理员角色绑定
('ADMIN_ROLE:BIND', '管理员绑定角色', '管理员角色', '/admin/admin-role/bind', 1, 120),
('ADMIN_ROLE:UNBIND', '管理员解绑角色', '管理员角色', '/admin/admin-role/unbind', 1, 130),
('ADMIN_ROLE:VIEW', '查看管理员角色', '管理员角色', '/admin/admin-role/list/**', 0, 140),

-- 景点类型管理
('ATTRACTION_TYPE:CREATE', '创建景点类型', '景点管理', '/admin/attraction-type/create', 0, 150),
('ATTRACTION_TYPE:DELETE', '删除景点类型', '景点管理', '/admin/attraction-type/delete/**', 0, 160),
('ATTRACTION_TYPE:UPDATE', '修改景点类型', '景点管理', '/admin/attraction-type/update', 0, 170),
('ATTRACTION_TYPE:VIEW', '查看景点类型', '景点管理', '/admin/attraction-type/**', 0, 180),

-- 景点管理(通用权限)
('ATTRACTION:MANAGE', '景点管理', '景点管理', '/admin/attraction/**', 0, 190),

-- 系统管理(超级权限)
('SYSTEM:MANAGE', '系统管理', '系统管理', '/admin/**', 1, 1000)
ON DUPLICATE KEY UPDATE 
    `permission_name` = VALUES(`permission_name`),
    `resource_type` = VALUES(`resource_type`),
    `resource_path` = VALUES(`resource_path`),
    `is_sensitive` = VALUES(`is_sensitive`),
    `sort_order` = VALUES(`sort_order`);

-- ----------------------------------------
-- 2. 插入系统角色数据
-- ----------------------------------------

INSERT INTO `admin_role` (`role_name`, `role_desc`, `status`) VALUES
('超级管理员', '拥有系统所有权限,可以管理所有模块', 1),
('权限管理员', '负责权限和角色的管理', 1),
('内容管理员', '负责景点类型和内容管理', 1),
('运营管理员', '负责日常运营相关功能', 1),
('数据查看员', '只能查看数据,不能修改', 1)
ON DUPLICATE KEY UPDATE 
    `role_desc` = VALUES(`role_desc`),
    `status` = VALUES(`status`);

-- ----------------------------------------
-- 3. 为角色分配权限
-- ----------------------------------------

-- 超级管理员 - 拥有所有权限
INSERT INTO `admin_role_permission` (`role_id`, `permission_id`)
SELECT 1, `permission_id` FROM `admin_permission`
ON DUPLICATE KEY UPDATE `role_id` = VALUES(`role_id`);

-- 权限管理员 - 权限和角色相关权限
INSERT INTO `admin_role_permission` (`role_id`, `permission_id`)
SELECT 2, `permission_id` FROM `admin_permission` 
WHERE `permission_code` IN (
    'PERMISSION:CREATE', 'PERMISSION:DELETE', 'PERMISSION:UPDATE', 'PERMISSION:VIEW',
    'ROLE:CREATE', 'ROLE:DELETE', 'ROLE:UPDATE', 'ROLE:VIEW',
    'ROLE_PERMISSION:BIND', 'ROLE_PERMISSION:UNBIND', 'ROLE_PERMISSION:VIEW',
    'ADMIN_ROLE:BIND', 'ADMIN_ROLE:UNBIND', 'ADMIN_ROLE:VIEW'
)
ON DUPLICATE KEY UPDATE `role_id` = VALUES(`role_id`);

-- 内容管理员 - 景点类型管理权限
INSERT INTO `admin_role_permission` (`role_id`, `permission_id`)
SELECT 3, `permission_id` FROM `admin_permission` 
WHERE `permission_code` IN (
    'ATTRACTION_TYPE:CREATE', 'ATTRACTION_TYPE:DELETE', 
    'ATTRACTION_TYPE:UPDATE', 'ATTRACTION_TYPE:VIEW',
    'ATTRACTION:MANAGE'
)
ON DUPLICATE KEY UPDATE `role_id` = VALUES(`role_id`);

-- 运营管理员 - 创建、修改、查看权限
INSERT INTO `admin_role_permission` (`role_id`, `permission_id`)
SELECT 4, `permission_id` FROM `admin_permission` 
WHERE `permission_code` IN (
    'ATTRACTION_TYPE:CREATE', 'ATTRACTION_TYPE:UPDATE', 'ATTRACTION_TYPE:VIEW'
)
ON DUPLICATE KEY UPDATE `role_id` = VALUES(`role_id`);

-- 数据查看员 - 仅查看权限
INSERT INTO `admin_role_permission` (`role_id`, `permission_id`)
SELECT 5, `permission_id` FROM `admin_permission` 
WHERE `permission_code` LIKE '%:VIEW'
ON DUPLICATE KEY UPDATE `role_id` = VALUES(`role_id`);

-- ----------------------------------------
-- 4. 为管理员分配角色(示例)
-- ----------------------------------------

-- 假设管理员ID为1的是超级管理员
INSERT INTO `admin_role_relation` (`admin_id`, `role_id`) VALUES (1, 1)
ON DUPLICATE KEY UPDATE `role_id` = VALUES(`role_id`);

-- ----------------------------------------
-- 5. 查询验证
-- ----------------------------------------

-- 查询所有权限
SELECT `permission_code`, `permission_name`, `resource_type`, `is_sensitive`, `sort_order` 
FROM `admin_permission` 
ORDER BY `sort_order`;

-- 查询所有角色
SELECT `role_id`, `role_name`, `role_desc`, `status` 
FROM `admin_role`;

-- 查询角色权限分配情况
SELECT 
    ar.`role_name`,
    COUNT(arp.`permission_id`) AS permission_count,
    GROUP_CONCAT(ap.`permission_code` ORDER BY ap.`sort_order` SEPARATOR ', ') AS permissions
FROM `admin_role` ar
LEFT JOIN `admin_role_permission` arp ON ar.`role_id` = arp.`role_id`
LEFT JOIN `admin_permission` ap ON arp.`permission_id` = ap.`permission_id`
GROUP BY ar.`role_id`, ar.`role_name`
ORDER BY ar.`role_id`;

-- 查询管理员角色分配情况
SELECT 
    a.`admin_id`,
    a.`username`,
    a.`full_name`,
    GROUP_CONCAT(ar.`role_name` SEPARATOR ', ') AS roles
FROM `admin` a
LEFT JOIN `admin_role_relation` arr ON a.`admin_id` = arr.`admin_id`
LEFT JOIN `admin_role` ar ON arr.`role_id` = ar.`role_id`
GROUP BY a.`admin_id`, a.`username`, a.`full_name`;

-- ========================================
-- 脚本执行完成
-- ========================================
