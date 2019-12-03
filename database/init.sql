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
insert into permission (id,code,name,description) values (9, 'system:permission:create', '创建权限角色', 'permission POST');
insert into permission (id,code,name,description) values (10, 'system:permission:update', '修改权限角色', 'permission PUT');
insert into permission (id,code,name,description) values (11, 'system:permission:delete', '删除权限角色', 'permission DELETE');
insert into permission (id,code,name,description) values (12, 'system:permission:read', '获取权限角色', 'permission GET');
-- 系统菜单
insert into permission (id,code,name,description) values (13, 'system:menu:create', '创建菜单角色', 'menu POST');
insert into permission (id,code,name,description) values (14, 'system:menu:update', '修改菜单角色', 'menu PUT');
insert into permission (id,code,name,description) values (15, 'system:menu:delete', '删除菜单角色', 'menu DELETE');
insert into permission (id,code,name,description) values (16, 'system:menu:read', '获取菜单角色', 'menu GET');
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
 