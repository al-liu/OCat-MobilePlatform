use ocat;
-- 初始化权限
-- 系统用户 
insert into permission (id,code,name,description) values (1, 'system:user:create', '创建系统用户', 'user POST');
insert into permission (id,code,name,description) values (2, 'system:user:update', '修改系统用户', 'user PUT');
insert into permission (id,code,name,description) values (3, 'system:user:delete', '删除系统用户', 'user DELETE');
insert into permission (id,code,name,description) values (4, 'system:user:read', '获取系统用户', 'user GET');
-- 系统角色
insert into permission (id,code,name,description) values (5, 'system:role:create', '创建系统角色', 'role POST');
insert into permission (id,code,name,description) values (6, 'system:role:update', '修改系统角色', 'role PUT');
insert into permission (id,code,name,description) values (7, 'system:role:delete', '删除系统角色', 'role DELETE');
insert into permission (id,code,name,description) values (8, 'system:role:read', '获取系统角色', 'role GET');
-- 系统权限
insert into permission (id,code,name,description) values (9, 'system:permission:create', '创建系统权限', 'permission POST');
insert into permission (id,code,name,description) values (10, 'system:permission:update', '修改系统权限', 'permission PUT');
insert into permission (id,code,name,description) values (11, 'system:permission:delete', '删除系统权限', 'permission DELETE');
insert into permission (id,code,name,description) values (12, 'system:permission:read', '获取系统权限', 'permission GET');
-- 系统菜单
insert into permission (id,code,name,description) values (13, 'system:menu:create', '创建系统菜单', 'menu POST');
insert into permission (id,code,name,description) values (14, 'system:menu:update', '修改系统菜单', 'menu PUT');
insert into permission (id,code,name,description) values (15, 'system:menu:delete', '删除系统菜单', 'menu DELETE');
insert into permission (id,code,name,description) values (16, 'system:menu:read', '获取系统菜单', 'menu GET');
-- 离线包管理
insert into permission (id,code,name,description) values (17, 'package:publish', '上传离线包', 'package publish POST');
insert into permission (id,code,name,description) values (18, 'package:fetch', '获取离线包', 'package fetch POST');
insert into permission (id,code,name,description) values (19, 'package:release', '发布离线包', 'package release POST');
-- 应用程序
insert into permission (id,code,name,description) values (20, 'application:create', '创建应用程序', 'application POST');
insert into permission (id,code,name,description) values (21, 'application:update', '修改应用程序', 'application PUT');
insert into permission (id,code,name,description) values (22, 'application:delete', '删除应用程序', 'application DELETE');
insert into permission (id,code,name,description) values (23, 'application:read', '获取应用程序', 'application GET');

-- 初始化角色
insert into role (id,code,name,description) values (1, 'system:admin', '系统管理员', '管理 user role menu permission');
insert into role (id,code,name,description) values (2, 'package:admin', '离线包管理员', '管理 package 的 publish，fetch，release');
insert into role (id,code,name,description) values (3, 'application:admin', '应用程序管理员', '管理 application');

-- 初始化角色与权限的绑定关系
-- 系统管理员
insert into role_permission (id,role_id,permission_id) values (1, 1, 1);
insert into role_permission (id,role_id,permission_id) values (2, 1, 2);
insert into role_permission (id,role_id,permission_id) values (3, 1, 3);
insert into role_permission (id,role_id,permission_id) values (4, 1, 4);
insert into role_permission (id,role_id,permission_id) values (5, 1, 5);
insert into role_permission (id,role_id,permission_id) values (6, 1, 6);
insert into role_permission (id,role_id,permission_id) values (7, 1, 7);
insert into role_permission (id,role_id,permission_id) values (8, 1, 8);
insert into role_permission (id,role_id,permission_id) values (9, 1, 9);
insert into role_permission (id,role_id,permission_id) values (10, 1, 10);
insert into role_permission (id,role_id,permission_id) values (11, 1, 11);
insert into role_permission (id,role_id,permission_id) values (12, 1, 12);
insert into role_permission (id,role_id,permission_id) values (13, 1, 13);
insert into role_permission (id,role_id,permission_id) values (14, 1, 14);
insert into role_permission (id,role_id,permission_id) values (15, 1, 15);
insert into role_permission (id,role_id,permission_id) values (16, 1, 16);
-- 离线包管理员
insert into role_permission (id,role_id,permission_id) values (17, 2, 17);
insert into role_permission (id,role_id,permission_id) values (18, 2, 18);
insert into role_permission (id,role_id,permission_id) values (19, 2, 19);
-- 应用程序管理员 
insert into role_permission (id,role_id,permission_id) values (20, 3, 20);
insert into role_permission (id,role_id,permission_id) values (21, 3, 21);
insert into role_permission (id,role_id,permission_id) values (22, 3, 22);
insert into role_permission (id,role_id,permission_id) values (23, 3, 23);

-- 初始化用户
-- a123
insert into user (id,username,password,description,enabled,salt) values (1, 'S-administrator', '42b1d70246486d7da27d91ca22a5b51b', '拥有所有权限的超管', 1, 'mmbSTHo0ONEeauusz7B52A==');
-- a321
insert into user (id,username,password,description,enabled,salt) values (2, 'package-administrator', '8444123de77cf181de313c1d5fe559ca', '拥有离线包的所有权限', 1, '9Qlv4I5KB6Tx1bIAV0EzOw==');
 
-- 初始化用户和角色关系
insert into user_role (id,user_id,role_id) values (1, 1, 1);
insert into user_role (id,user_id,role_id) values (2, 1, 2);
insert into user_role (id,user_id,role_id) values (3, 1, 3);
insert into user_role (id,user_id,role_id) values (4, 2, 2);
 
--  菜单
-- 一级菜单
insert into menu (id,parent_id,order_num,type,name,icon,href,description) values (1, 0, 1, 1, '系统管理', 'el-icon-user', '', '一级目录');
insert into menu (id,parent_id,order_num,type,name,icon,href,description) values (2, 0, 2, 1, '离线包管理', 'el-icon-cherry', '', '一级目录');
-- 二级菜单
insert into menu (id,parent_id,order_num,type,name,icon,href,description) values (3, 1, 1, 1, '用户', 'el-icon-user', '/users', '二级目录');
insert into menu (id,parent_id,order_num,type,name,icon,href,description) values (4, 1, 2, 1, '角色', 'el-icon-turn-off', '/roles', '二级目录');
insert into menu (id,parent_id,order_num,type,name,icon,href,description) values (5, 1, 3, 1, '权限', 'el-icon-bangzhu', '/permissions', '二级目录');
insert into menu (id,parent_id,order_num,type,name,icon,href,description) values (6, 1, 4, 1, '菜单', 'el-icon-fork-spoon', '/menus', '二级目录');
insert into menu (id,parent_id,order_num,type,name,icon,href,description) values (7, 2, 1, 1, '应用程序', 'el-icon-bicycle', '/applications', '二级目录');
-- 三级按钮
-- 用户
insert into menu (id,parent_id,order_num,type,name,icon,href,description) values (8, 3, 1, 2, '用户编辑', '', '', '三级按钮');
insert into menu (id,parent_id,order_num,type,name,icon,href,description) values (9, 3, 2, 2, '用户删除', '', '', '三级按钮');
insert into menu (id,parent_id,order_num,type,name,icon,href,description) values (10, 3, 3, 2, '用户角色分配', '', '', '三级按钮');
insert into menu (id,parent_id,order_num,type,name,icon,href,description) values (11, 3, 4, 2, '用户锁定', '', '', '三级按钮');
insert into menu (id,parent_id,order_num,type,name,icon,href,description) values (12, 3, 5, 2, '用户创建', '', '', '三级按钮');
-- 角色
insert into menu (id,parent_id,order_num,type,name,icon,href,description) values (13, 4, 1, 2, '角色创建', '', '', '三级按钮');
insert into menu (id,parent_id,order_num,type,name,icon,href,description) values (14, 4, 2, 2, '角色编辑', '', '', '三级按钮');
insert into menu (id,parent_id,order_num,type,name,icon,href,description) values (15, 4, 3, 2, '角色删除', '', '', '三级按钮');
insert into menu (id,parent_id,order_num,type,name,icon,href,description) values (16, 4, 4, 2, '角色权限分配', '', '', '三级按钮');
insert into menu (id,parent_id,order_num,type,name,icon,href,description) values (17, 4, 5, 2, '角色菜单分配', '', '', '三级按钮');
-- 权限
insert into menu (id,parent_id,order_num,type,name,icon,href,description) values (18, 5, 1, 2, '权限创建', '', '', '三级按钮');
insert into menu (id,parent_id,order_num,type,name,icon,href,description) values (19, 5, 2, 2, '权限编辑', '', '', '三级按钮');
insert into menu (id,parent_id,order_num,type,name,icon,href,description) values (20, 5, 3, 2, '权限删除', '', '', '三级按钮');
-- 菜单
insert into menu (id,parent_id,order_num,type,name,icon,href,description) values (21, 6, 1, 2, '菜单创建', '', '', '三级按钮');
insert into menu (id,parent_id,order_num,type,name,icon,href,description) values (22, 6, 2, 2, '菜单编辑', '', '', '三级按钮');
insert into menu (id,parent_id,order_num,type,name,icon,href,description) values (23, 6, 3, 2, '菜单删除', '', '', '三级按钮');
-- 应用
insert into menu (id,parent_id,order_num,type,name,icon,href,description) values (24, 7, 1, 2, '应用创建', '', '', '三级按钮');
insert into menu (id,parent_id,order_num,type,name,icon,href,description) values (25, 7, 2, 2, '应用编辑', '', '', '三级按钮');
insert into menu (id,parent_id,order_num,type,name,icon,href,description) values (26, 7, 3, 2, '应用删除', '', '', '三级按钮');
insert into menu (id,parent_id,order_num,type,name,icon,href,description) values (27, 7, 4, 2, '离线包', '', '', '三级按钮');
-- 离线包
insert into menu (id,parent_id,order_num,type,name,icon,href,description) values (28, 27, 1, 2, '创建离线包', '', '', '三级按钮');
insert into menu (id,parent_id,order_num,type,name,icon,href,description) values (29, 27, 2, 2, '删除离线包', '', '', '三级按钮');
insert into menu (id,parent_id,order_num,type,name,icon,href,description) values (30, 27, 3, 2, '发布离线包', '', '', '三级按钮');
insert into menu (id,parent_id,order_num,type,name,icon,href,description) values (31, 27, 4, 2, '查看补丁包', '', '', '三级按钮');

insert into role_menu (id, role_id, menu_id) values (1, 1, 1);
insert into role_menu (id, role_id, menu_id) values (2, 1, 3);
insert into role_menu (id, role_id, menu_id) values (3, 1, 4);
insert into role_menu (id, role_id, menu_id) values (4, 1, 5);
insert into role_menu (id, role_id, menu_id) values (5, 1, 6);
insert into role_menu (id, role_id, menu_id) values (6, 2, 2);
insert into role_menu (id, role_id, menu_id) values (7, 2, 7);