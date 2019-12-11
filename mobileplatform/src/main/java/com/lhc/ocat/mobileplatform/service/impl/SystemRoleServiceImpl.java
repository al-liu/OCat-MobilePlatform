package com.lhc.ocat.mobileplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lhc.ocat.mobileplatform.domain.dos.*;
import com.lhc.ocat.mobileplatform.domain.dto.Menu;
import com.lhc.ocat.mobileplatform.domain.dto.Permission;
import com.lhc.ocat.mobileplatform.domain.dto.Role;
import com.lhc.ocat.mobileplatform.exception.ApiErrorType;
import com.lhc.ocat.mobileplatform.exception.ApiException;
import com.lhc.ocat.mobileplatform.mapper.*;
import com.lhc.ocat.mobileplatform.service.SystemRoleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lhc
 * @date 2019-11-27 22:10
 */
@Service
public class SystemRoleServiceImpl implements SystemRoleService {
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private PermissionMapper permissionMapper;
    @Autowired
    private MenuMapper menuMapper;
    @Autowired
    private RolePermissionMapper rolePermissionMapper;
    @Autowired
    private RoleMenuMapper roleMenuMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public void createRole(String code, String name, String description) throws ApiException {
        RoleDO roleDO = new RoleDO();
        roleDO.setCode(code);
        roleDO.setName(name);
        roleDO.setDescription(description);
        roleMapper.insert(roleDO);
    }

    @Override
    public void updateRole(Long roleId, String code, String name, String description) throws ApiException {
        RoleDO roleDO = roleMapper.selectById(roleId);
        if (Objects.isNull(roleDO)) {
            throw new ApiException(ApiErrorType.SYSTEM_ROLE_NOT_FOUND_ERROR);
        }
        roleDO.setCode(code);
        roleDO.setName(name);
        roleDO.setDescription(description);
        roleMapper.updateById(roleDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long roleId) throws ApiException {
        RoleDO roleDO = roleMapper.selectById(roleId);
        if (Objects.isNull(roleDO)) {
            throw new ApiException(ApiErrorType.SYSTEM_ROLE_NOT_FOUND_ERROR);
        }
        // 删除指定 roleId 的用户-角色表 中所有数据
        userRoleMapper.delete(new LambdaQueryWrapper<UserRoleDO>().eq(UserRoleDO::getRoleId, roleId));
        roleMapper.deleteById(roleId);
    }

    @Override
    public List<Role> listRole() throws ApiException {
        List<RoleDO> roleDOList = roleMapper.selectList(
                new LambdaQueryWrapper<RoleDO>()
                        .orderByDesc(RoleDO::getCreateTime));
        List<Role> roleList = new ArrayList<>();
        for (RoleDO roleDO :
                roleDOList) {
            Role role = Role.toRole(roleDO);
            roleList.add(role);
        }
        return roleList;
    }

    @Override
    public List<Permission> listPermissionByRoleId(Long roleId) throws ApiException {
        List<PermissionDO> permissionDOList = roleMapper.listPermissions(roleId);
        List<Permission> permissionList = new ArrayList<>();
        for (PermissionDO permissionDO :
                permissionDOList) {
            Permission permission = Permission.toPermission(permissionDO);
            permissionList.add(permission);
        }
        return permissionList;
    }

    @Override
    public List<Menu> listMenuByRoleId(Long roleId) throws ApiException {
        List<MenuDO> menuDOList = roleMapper.listMenu(roleId);
        List<Menu> menuList = new ArrayList<>();
        List<String> parentIdList = new ArrayList<>();
        for (MenuDO menuDO :
                menuDOList) {
            Menu menu = Menu.toMenu(menuDO);
            menuList.add(menu);
            parentIdList.add(menu.getParentId());
        }
        // **将查询结果去掉作为其他父菜单的菜单项，用于前端组件使用**
        return menuList.stream().filter(menu -> !parentIdList.contains(menu.getId())).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void allotPermissions(Long roleId, Set<String> permissionIdList) throws ApiException {
        RoleDO roleDO = roleMapper.selectById(roleId);
        if (Objects.isNull(roleDO)) {
            throw new ApiException(ApiErrorType.SYSTEM_ROLE_NOT_FOUND_ERROR);
        }
        // 批量分配权限，需要先清除之前的所有权限
        rolePermissionMapper.delete(new LambdaQueryWrapper<RolePermissionDO>().eq(RolePermissionDO::getRoleId, roleId));
        for (String permissionId :
                permissionIdList) {
            PermissionDO permissionDO = permissionMapper.selectById(permissionId);
            if (Objects.isNull(permissionDO)) {
                throw new ApiException(ApiErrorType.SYSTEM_PERMISSION_NOT_FOUND_ERROR);
            }
            RolePermissionDO rolePermissionDO = RolePermissionDO.builder()
                    .roleId(roleId)
                    .permissionId(Long.valueOf(permissionId)).build();
            rolePermissionMapper.insert(rolePermissionDO);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void allotMenus(Long roleId, Set<String> menuIdList) throws ApiException {
        RoleDO roleDO = roleMapper.selectById(roleId);
        if (Objects.isNull(roleDO)) {
            throw new ApiException(ApiErrorType.SYSTEM_ROLE_NOT_FOUND_ERROR);
        }
        // 批量分配菜单，需要先清除之前的所有菜单
        roleMenuMapper.delete(new LambdaQueryWrapper<RoleMenuDO>().eq(RoleMenuDO::getRoleId, roleId));
        for (String menuId :
                menuIdList) {
            if (!menuId.equals(Menu.ROOT_PARENT_ID)) {
                MenuDO menuDO = menuMapper.selectById(menuId);
                if (Objects.isNull(menuDO)) {
                    throw new ApiException(ApiErrorType.SYSTEM_MENU_NOT_FOUND_ERROR);
                }
                RoleMenuDO roleMenuDO = RoleMenuDO.builder()
                        .roleId(roleId)
                        .menuId(Long.valueOf(menuId)).build();
                roleMenuMapper.insert(roleMenuDO);
            }
        }
    }
}
