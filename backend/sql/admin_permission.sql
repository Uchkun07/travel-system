-- ============================================================
-- 管理员权限表初始化脚本
-- 生成时间: 2025-11-26
-- 说明: 包含所有管理端Controller的权限定义
-- ============================================================

-- 清空现有权限数据(谨慎使用)
-- TRUNCATE TABLE admin_permission;

-- ============================================================
-- 1. 系统管理权限 (SYSTEM:MANAGE)
-- ============================================================
INSERT INTO admin_permission (permission_code, permission_name, resource_type, resource_path, is_sensitive, sort_order)
VALUES ('SYSTEM:MANAGE', '系统管理', '系统', '/*', 1, 1);

-- ============================================================
-- 2. 管理员管理权限 (ADMIN)
-- ============================================================
INSERT INTO admin_permission (permission_code, permission_name, resource_type, resource_path, is_sensitive, sort_order)
VALUES 
('ADMIN:CREATE', '创建管理员', '管理员管理', '/api/admin/create', 1, 10),
('ADMIN:DELETE', '删除管理员', '管理员管理', '/api/admin/delete/**', 1, 11),
('ADMIN:UPDATE', '修改管理员', '管理员管理', '/api/admin/update', 1, 12),
('ADMIN:VIEW', '查看管理员', '管理员管理', '/api/admin/list', 0, 13);

-- ============================================================
-- 3. 角色管理权限 (ROLE)
-- ============================================================
INSERT INTO admin_permission (permission_code, permission_name, resource_type, resource_path, is_sensitive, sort_order)
VALUES 
('ROLE:CREATE', '创建角色', '角色管理', '/api/admin/role/create', 1, 20),
('ROLE:DELETE', '删除角色', '角色管理', '/api/admin/role/delete/**', 1, 21),
('ROLE:UPDATE', '修改角色', '角色管理', '/api/admin/role/update', 1, 22),
('ROLE:VIEW', '查看角色', '角色管理', '/api/admin/role/list', 0, 23);

-- ============================================================
-- 4. 权限管理权限 (PERMISSION)
-- ============================================================
INSERT INTO admin_permission (permission_code, permission_name, resource_type, resource_path, is_sensitive, sort_order)
VALUES 
('PERMISSION:CREATE', '创建权限', '权限管理', '/api/admin/permission/create', 1, 30),
('PERMISSION:DELETE', '删除权限', '权限管理', '/api/admin/permission/delete/**', 1, 31),
('PERMISSION:UPDATE', '修改权限', '权限管理', '/api/admin/permission/update', 1, 32),
('PERMISSION:VIEW', '查看权限', '权限管理', '/api/admin/permission/list', 0, 33);

-- ============================================================
-- 5. 角色权限绑定管理 (ROLE_PERMISSION)
-- ============================================================
INSERT INTO admin_permission (permission_code, permission_name, resource_type, resource_path, is_sensitive, sort_order)
VALUES 
('ROLE_PERMISSION:BIND', '角色绑定权限', '角色权限管理', '/api/admin/role-permission/bind', 1, 40),
('ROLE_PERMISSION:UNBIND', '角色解绑权限', '角色权限管理', '/api/admin/role-permission/unbind', 1, 41),
('ROLE_PERMISSION:VIEW', '查看角色权限', '角色权限管理', '/api/admin/role-permission/list/**', 0, 42);

-- ============================================================
-- 6. 管理员角色绑定管理 (ADMIN_ROLE)
-- ============================================================
INSERT INTO admin_permission (permission_code, permission_name, resource_type, resource_path, is_sensitive, sort_order)
VALUES 
('ADMIN_ROLE:BIND', '管理员绑定角色', '管理员角色管理', '/api/admin/admin-role/bind', 1, 50),
('ADMIN_ROLE:UNBIND', '管理员解绑角色', '管理员角色管理', '/api/admin/admin-role/unbind', 1, 51),
('ADMIN_ROLE:VIEW', '查看管理员角色', '管理员角色管理', '/api/admin/admin-role/list/**', 0, 52);

-- ============================================================
-- 7. 操作日志管理权限 (OPERATION_LOG)
-- ============================================================
INSERT INTO admin_permission (permission_code, permission_name, resource_type, resource_path, is_sensitive, sort_order)
VALUES 
('OPERATION_LOG:DELETE', '删除操作日志', '操作日志管理', '/api/admin/operation-log/delete/**', 1, 60),
('OPERATION_LOG:VIEW', '查看操作日志', '操作日志管理', '/api/admin/operation-log/list', 0, 61);

-- ============================================================
-- 8. 用户管理权限 (USER)
-- ============================================================
INSERT INTO admin_permission (permission_code, permission_name, resource_type, resource_path, is_sensitive, sort_order)
VALUES 
('USER:VIEW', '查看用户', '用户管理', '/api/admin/user/list', 0, 70);

-- ============================================================
-- 9. 用户标签管理权限 (USER_TAG)
-- ============================================================
INSERT INTO admin_permission (permission_code, permission_name, resource_type, resource_path, is_sensitive, sort_order)
VALUES 
('USER_TAG:CREATE', '创建用户标签', '用户标签管理', '/api/admin/user/tag-dict/create', 0, 80),
('USER_TAG:DELETE', '删除用户标签', '用户标签管理', '/api/admin/user/tag-dict/delete/**', 0, 81),
('USER_TAG:UPDATE', '修改用户标签', '用户标签管理', '/api/admin/user/tag-dict/update', 0, 82),
('USER_TAG:VIEW', '查看用户标签', '用户标签管理', '/api/admin/user/tag-dict/list', 0, 83),
('USER_TAG:BIND', '用户绑定标签', '用户标签管理', '/api/admin/user/tag/bind', 0, 84),
('USER_TAG:UNBIND', '用户解绑标签', '用户标签管理', '/api/admin/user/tag/unbind', 0, 85);

-- ============================================================
-- 10. 轮播图管理权限 (SLIDESHOW)
-- ============================================================
INSERT INTO admin_permission (permission_code, permission_name, resource_type, resource_path, is_sensitive, sort_order)
VALUES 
('SLIDESHOW:CREATE', '创建轮播图', '轮播图管理', '/api/slideshow/create', 0, 90),
('SLIDESHOW:DELETE', '删除轮播图', '轮播图管理', '/api/slideshow/delete/**', 0, 91),
('SLIDESHOW:UPDATE', '修改轮播图', '轮播图管理', '/api/slideshow/update', 0, 92),
('SLIDESHOW:VIEW', '查看轮播图', '轮播图管理', '/api/slideshow/list', 0, 93);

-- ============================================================
-- 11. 景点管理权限 (ATTRACTION)
-- ============================================================
INSERT INTO admin_permission (permission_code, permission_name, resource_type, resource_path, is_sensitive, sort_order)
VALUES 
('ATTRACTION:CREATE', '创建景点', '景点管理', '/api/admin/attraction/create', 0, 100),
('ATTRACTION:DELETE', '删除景点', '景点管理', '/api/admin/attraction/delete/**', 0, 101),
('ATTRACTION:UPDATE', '修改景点', '景点管理', '/api/admin/attraction/update', 0, 102),
('ATTRACTION:LIST', '查看景点列表', '景点管理', '/api/admin/attraction/list', 0, 103),
('ATTRACTION:DETAIL', '查看景点详情', '景点管理', '/api/admin/attraction/detail/**', 0, 104);

-- ============================================================
-- 12. 景点类型管理权限 (ATTRACTION_TYPE)
-- ============================================================
INSERT INTO admin_permission (permission_code, permission_name, resource_type, resource_path, is_sensitive, sort_order)
VALUES 
('ATTRACTION_TYPE:CREATE', '创建景点类型', '景点类型管理', '/api/admin/attraction/type/create', 0, 110),
('ATTRACTION_TYPE:DELETE', '删除景点类型', '景点类型管理', '/api/admin/attraction/type/delete/**', 0, 111),
('ATTRACTION_TYPE:UPDATE', '修改景点类型', '景点类型管理', '/api/admin/attraction/type/update', 0, 112),
('ATTRACTION_TYPE:VIEW', '查看景点类型', '景点类型管理', '/api/admin/attraction/type/list', 0, 113);

-- ============================================================
-- 13. 城市管理权限 (CITY)
-- ============================================================
INSERT INTO admin_permission (permission_code, permission_name, resource_type, resource_path, is_sensitive, sort_order)
VALUES 
('CITY:CREATE', '创建城市', '城市管理', '/api/admin/attraction/city/create', 0, 120),
('CITY:DELETE', '删除城市', '城市管理', '/api/admin/attraction/city/delete/**', 0, 121),
('CITY:UPDATE', '修改城市', '城市管理', '/api/admin/attraction/city/update', 0, 122),
('CITY:VIEW', '查看城市', '城市管理', '/api/admin/attraction/city/list', 0, 123);

-- ============================================================
-- 14. 景点标签管理权限 (ATTRACTION_TAG)
-- ============================================================
INSERT INTO admin_permission (permission_code, permission_name, resource_type, resource_path, is_sensitive, sort_order)
VALUES 
('ATTRACTION_TAG:CREATE', '创建景点标签', '景点标签管理', '/api/admin/attraction/tag/create', 0, 130),
('ATTRACTION_TAG:DELETE', '删除景点标签', '景点标签管理', '/api/admin/attraction/tag/delete/**', 0, 131),
('ATTRACTION_TAG:UPDATE', '修改景点标签', '景点标签管理', '/api/admin/attraction/tag/update', 0, 132),
('ATTRACTION_TAG:VIEW', '查看景点标签', '景点标签管理', '/api/admin/attraction/tag/list', 0, 133);

-- ============================================================
-- 15. 景点标签关联管理权限 (ATTRACTION_TAG_RELATION)
-- ============================================================
INSERT INTO admin_permission (permission_code, permission_name, resource_type, resource_path, is_sensitive, sort_order)
VALUES 
('ATTRACTION_TAG_RELATION:BIND', '景点绑定标签', '景点标签关联管理', '/api/admin/attraction/tag-relation/bind', 0, 140),
('ATTRACTION_TAG_RELATION:UNBIND', '景点解绑标签', '景点标签关联管理', '/api/admin/attraction/tag-relation/unbind', 0, 141),
('ATTRACTION_TAG_RELATION:VIEW', '查看景点标签关联', '景点标签关联管理', '/api/admin/attraction/tag-relation/list/**', 0, 142);

-- ============================================================
-- 16. 文件管理权限 (FILE)
-- ============================================================
INSERT INTO admin_permission (permission_code, permission_name, resource_type, resource_path, is_sensitive, sort_order)
VALUES 
('FILE:UPLOAD', '上传文件', '文件管理', '/api/upload/**', 0, 150),
('FILE:DELETE', '删除文件', '文件管理', '/api/upload', 0, 151);

-- ============================================================
-- 查询插入结果
-- ============================================================
SELECT 
    permission_id,
    permission_code,
    permission_name,
    resource_type,
    is_sensitive,
    sort_order
FROM admin_permission
ORDER BY sort_order;

-- ============================================================
-- 统计信息
-- ============================================================
SELECT 
    resource_type AS '资源类型',
    COUNT(*) AS '权限数量',
    SUM(CASE WHEN is_sensitive = 1 THEN 1 ELSE 0 END) AS '敏感权限数',
    SUM(CASE WHEN is_sensitive = 0 THEN 1 ELSE 0 END) AS '普通权限数'
FROM admin_permission
GROUP BY resource_type
ORDER BY sort_order;
