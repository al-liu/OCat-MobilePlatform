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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    public void deleteRole(Long roleId) throws ApiException {
        RoleDO roleDO = roleMapper.selectById(roleId);
        if (Objects.isNull(roleDO)) {
            throw new ApiException(ApiErrorType.SYSTEM_ROLE_NOT_FOUND_ERROR);
        }
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
            Role role = new Role();
            BeanUtils.copyProperties(roleDO, role);
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
            Permission permission = new Permission();
            BeanUtils.copyProperties(permissionDO, permission);
            permissionList.add(permission);
        }
        return permissionList;
    }

    @Override
    public List<Menu> listMenuByRoleId(Long roleId) throws ApiException {
        List<MenuDO> menuDOList = roleMapper.listMenu(roleId);
        List<Menu> menuList = new ArrayList<>();
        for (MenuDO menuDO :
                menuDOList) {
            Menu menu = new Menu();
            BeanUtils.copyProperties(menuDO, menu);
            menuList.add(menu);
        }
        return menuList;
    }

    @Override
    public void allotPermission(Long roleId, Long permissionId) throws ApiException {
        RoleDO roleDO = roleMapper.selectById(roleId);
        if (Objects.isNull(roleDO)) {
            throw new ApiException(ApiErrorType.SYSTEM_ROLE_NOT_FOUND_ERROR);
        }
        PermissionDO permissionDO = permissionMapper.selectById(permissionId);
        if (Objects.isNull(permissionDO)) {
            throw new ApiException(ApiErrorType.SYSTEM_PERMISSION_NOT_FOUND_ERROR);
        }
        RolePermissionDO rolePermissionDO = RolePermissionDO.builder()
                .roleId(roleId)
                .permissionId(permissionId).build();
        rolePermissionMapper.insert(rolePermissionDO);
    }

    @Override
    public void allotMenu(Long roleId, Long menuId) throws ApiException {
        RoleDO roleDO = roleMapper.selectById(roleId);
        if (Objects.isNull(roleDO)) {
            throw new ApiException(ApiErrorType.SYSTEM_ROLE_NOT_FOUND_ERROR);
        }
        MenuDO menuDO = menuMapper.selectById(menuId);
        if (Objects.isNull(menuDO)) {
            throw new ApiException(ApiErrorType.SYSTEM_MENU_NOT_FOUND_ERROR);
        }
        RoleMenuDO roleMenuDO = RoleMenuDO.builder()
                .roleId(roleId)
                .menuId(menuId).build();
        roleMenuMapper.insert(roleMenuDO);
    }
}
