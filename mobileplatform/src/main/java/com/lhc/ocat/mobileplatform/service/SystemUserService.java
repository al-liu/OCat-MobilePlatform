package com.lhc.ocat.mobileplatform.service;

import com.lhc.ocat.mobileplatform.domain.dto.Menu;
import com.lhc.ocat.mobileplatform.domain.dto.Role;
import com.lhc.ocat.mobileplatform.domain.dto.User;
import com.lhc.ocat.mobileplatform.exception.ApiException;

import java.util.List;
import java.util.Set;

/**
 * @author lhc
 * @date 2019-11-27 19:44
 */
public interface SystemUserService {

    /**
     * 创建用户
     * @param username 用户名
     * @param password 密码
     * @param description 描述
     * @throws ApiException 业务异常
     */
    void createUser(String username, String password, String description) throws ApiException;

    /**
     * 修改用户
     * @param userId 用户id
     * @param username 用户名
     * @param password 密码
     * @param description 描述
     * @throws ApiException 业务异常
     */
    void updateUser(Long userId, String username, String password, String description) throws ApiException;

    /**
     * 删除用户
     * @param userId 用户id
     * @throws ApiException 业务异常
     */
    void deleteUser(Long userId) throws ApiException;

    /**
     * 查询所有系统用户
     * @return 用户列表
     * @throws ApiException 业务异常
     */
    List<User> listUser() throws ApiException;

    /**
     * 查询指定用户的所有角色
     * @param userId 用户id
     * @return 角色列表
     * @throws ApiException 业务异常
     */
    List<Role> listRoleByUserId(Long userId) throws ApiException;

    /**
     * 分配角色
     * @param userId 用户id
     * @param roleId 角色id
     * @throws ApiException 业务异常
     */
    void allotRole(Long userId, Long roleId) throws ApiException;

    /**
     * 分配多个角色
     * @param userId 用户id
     * @param roleIdList 角色id 列表
     * @throws ApiException 业务异常
     */
    void allotRoles(Long userId, Set<String> roleIdList) throws ApiException;

    /**
     * 设置用户是否可用
     * @param userId 用户id
     * @param enabled 是否可用
     * @throws ApiException 业务异常
     */
    void enableUser(Long userId, Integer enabled) throws ApiException;

    /**
     * 查询指定用户的菜单列表
     * @param userId 用户id
     * @return 获取menu集合
     * @throws ApiException 业务异常
     */
    Set<Menu> listMenus(Long userId) throws ApiException;
}
