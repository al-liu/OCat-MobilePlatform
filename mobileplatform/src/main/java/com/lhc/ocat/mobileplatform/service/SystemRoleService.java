package com.lhc.ocat.mobileplatform.service;

import com.lhc.ocat.mobileplatform.domain.dto.Menu;
import com.lhc.ocat.mobileplatform.domain.dto.Permission;
import com.lhc.ocat.mobileplatform.domain.dto.Role;
import com.lhc.ocat.mobileplatform.exception.ApiException;

import java.util.List;
import java.util.Set;

/**
 * @author lhc
 * @date 2019-11-27 22:05
 */
public interface SystemRoleService {

    /**
     * 创建角色
     * @param code 角色编号
     * @param name 角色名称
     * @param description 角色描述
     * @throws ApiException 业务异常
     */
    void createRole(String code, String name, String description) throws ApiException;

    /**
     * 修改角色
     * @param roleId 角色id
     * @param code 编号
     * @param name 名称
     * @param description 描述
     * @throws ApiException 业务异常
     */
    void updateRole(Long roleId, String code, String name, String description) throws ApiException;

    /**
     * 删除角色
     * @param roleId 角色id
     * @throws ApiException 业务异常
     */
    void deleteRole(Long roleId) throws ApiException;

    /**
     * 查询所有角色
     * @return 角色列表
     * @throws ApiException 业务异常
     */
    List<Role> listRole() throws ApiException;

    /**
     * 查询指定角色的所有权限
     * @param roleId 角色id
     * @return 权限列表
     * @throws ApiException 业务异常
     */
    List<Permission> listPermissionByRoleId(Long roleId) throws ApiException;

    /**
     * 查询指定角色的所有菜单
     * @param roleId 角色id
     * @return 菜单列表
     * @throws ApiException 业务异常
     */
    List<Menu> listMenuByRoleId(Long roleId) throws ApiException;

    /**
     * 分配多个权限
     * @param roleId 角色 id
     * @param permissionIdList 多个权限 id
     * @throws ApiException 业务异常
     */
    void allotPermissions(Long roleId, Set<String> permissionIdList) throws ApiException;

    /**
     * 分配多个菜单
     * @param roleId 菜单 id
     * @param menuIdList 多个菜单 id
     * @throws ApiException 业务异常
     */
    void allotMenus(Long roleId, Set<String> menuIdList) throws ApiException;
}
