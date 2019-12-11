package com.lhc.ocat.mobileplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lhc.ocat.mobileplatform.domain.dos.MenuDO;
import com.lhc.ocat.mobileplatform.domain.dos.RoleDO;
import com.lhc.ocat.mobileplatform.domain.dos.UserDO;
import com.lhc.ocat.mobileplatform.domain.dos.UserRoleDO;
import com.lhc.ocat.mobileplatform.domain.dto.Menu;
import com.lhc.ocat.mobileplatform.domain.dto.Role;
import com.lhc.ocat.mobileplatform.domain.dto.User;
import com.lhc.ocat.mobileplatform.exception.ApiErrorType;
import com.lhc.ocat.mobileplatform.exception.ApiException;
import com.lhc.ocat.mobileplatform.mapper.MenuMapper;
import com.lhc.ocat.mobileplatform.mapper.RoleMapper;
import com.lhc.ocat.mobileplatform.mapper.UserMapper;
import com.lhc.ocat.mobileplatform.mapper.UserRoleMapper;
import com.lhc.ocat.mobileplatform.service.SystemMenuService;
import com.lhc.ocat.mobileplatform.service.SystemRoleService;
import com.lhc.ocat.mobileplatform.service.SystemUserService;
import com.lhc.ocat.mobileplatform.systemmanage.ShiroConfig;
import lombok.extern.log4j.Log4j2;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lhc
 * @date 2019-11-27 19:45
 */
@Service
@Log4j2
public class SystemUserServiceImpl implements SystemUserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private SystemRoleService roleService;

    @Override
    public void createUser(String username, String password, String description) throws ApiException {
        String salt = new SecureRandomNumberGenerator().nextBytes().toString();
        String encodePassword = new SimpleHash(
                ShiroConfig.HASH_ALGORITHM_NAME,
                password,
                salt,
                ShiroConfig.HASH_ITERATION_TIME).toString();
        UserDO userDO = new UserDO();
        userDO.setUsername(username);
        userDO.setPassword(encodePassword);
        userDO.setDescription(description);
        userDO.setSalt(salt);
        userDO.setEnabled(UserDO.ENABLED);
        userMapper.insert(userDO);
    }

    @Override
    public void updateUser(Long userId, String username, String password, String description) throws ApiException {
        UserDO userDO = userMapper.selectById(userId);
        if (Objects.isNull(userDO)) {
            throw new ApiException(ApiErrorType.SYSTEM_USER_NOT_FOUND_ERROR);
        }
        String encodePassword = new SimpleHash(
                ShiroConfig.HASH_ALGORITHM_NAME,
                password,
                userDO.getSalt(),
                ShiroConfig.HASH_ITERATION_TIME).toString();
        userDO.setUsername(username);
        userDO.setPassword(encodePassword);
        userDO.setDescription(description);
        userMapper.updateById(userDO);
    }

    @Override
    public void deleteUser(Long userId) throws ApiException {
        UserDO userDO = userMapper.selectById(userId);
        if (Objects.isNull(userDO)) {
            throw new ApiException(ApiErrorType.SYSTEM_USER_NOT_FOUND_ERROR);
        }
        // 删除指定 userId 的用户-角色表 中所有数据
        userRoleMapper.delete(new LambdaQueryWrapper<UserRoleDO>().eq(UserRoleDO::getUserId, userId));
        userMapper.deleteById(userDO.getId());
    }

    @Override
    public List<User> listUser() throws ApiException {
        List<UserDO> list = userMapper.selectList(new LambdaQueryWrapper<UserDO>().orderByDesc(UserDO::getCreateTime));
        List<User> userList = new ArrayList<>();
        for (UserDO userDO:
             list) {
            User user = User.toUser(userDO);
            userList.add(user);
        }
        return userList;
    }

    @Override
    public List<Role> listRoleByUserId(Long userId) throws ApiException {
        UserDO userDO = userMapper.selectById(userId);
        if (Objects.isNull(userDO)) {
            throw new ApiException(ApiErrorType.SYSTEM_USER_NOT_FOUND_ERROR);
        }
        List<RoleDO> list = userMapper.listRoles(userId);
        List<Role> roleList = new ArrayList<>();
        for (RoleDO roleDO :
                list) {
            Role role = Role.toRole(roleDO);
            roleList.add(role);
        }
        return roleList;
    }

    @Override
    public void allotRole(Long userId, Long roleId) throws ApiException {
        UserDO userDO = userMapper.selectById(userId);
        if (Objects.isNull(userDO)) {
            throw new ApiException(ApiErrorType.SYSTEM_USER_NOT_FOUND_ERROR);
        }
        RoleDO roleDO = roleMapper.selectById(roleId);
        if (Objects.isNull(roleDO)) {
            throw new ApiException(ApiErrorType.SYSTEM_ROLE_NOT_FOUND_ERROR);
        }
        UserRoleDO userRoleDO = new UserRoleDO();
        userRoleDO.setUserId(userId);
        userRoleDO.setRoleId(roleId);
        userRoleMapper.insert(userRoleDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void allotRoles(Long userId, Set<String> roleIdList) throws ApiException {
        UserDO userDO = userMapper.selectById(userId);
        if (Objects.isNull(userDO)) {
            throw new ApiException(ApiErrorType.SYSTEM_USER_NOT_FOUND_ERROR);
        }
        // 先删除之前的所有 user role 关系
        userRoleMapper.delete(new LambdaQueryWrapper<UserRoleDO>().eq(UserRoleDO::getUserId, userId));
        for (String roleId :
                roleIdList) {
            RoleDO roleDO = roleMapper.selectById(Long.valueOf(roleId));
            if (Objects.isNull(roleDO)) {
                throw new ApiException(ApiErrorType.SYSTEM_ROLE_NOT_FOUND_ERROR);
            }
            UserRoleDO userRoleDO = new UserRoleDO();
            userRoleDO.setUserId(userId);
            userRoleDO.setRoleId(Long.valueOf(roleId));
            userRoleMapper.insert(userRoleDO);
        }
    }

    @Override
    public void enableUser(Long userId, Integer enabled) throws ApiException {
        UserDO userDO = userMapper.selectById(userId);
        if (Objects.isNull(userDO)) {
            throw new ApiException(ApiErrorType.SYSTEM_USER_NOT_FOUND_ERROR);
        }
        if (!UserDO.ENABLED.equals(enabled) && !UserDO.DISABLED.equals(enabled)) {
            throw new ApiException(ApiErrorType.SYSTEM_USER_ENABLE_ILLEGAL_ERROR);
        }
        userDO.setEnabled(enabled);
        userMapper.updateById(userDO);
    }

    @Override
    public Set<Menu> listMenus(Long userId) throws ApiException {
        List<Role> roleList = listRoleByUserId(userId);
        Set<Menu> menuSet = new HashSet<>();
        for (Role role :
                roleList) {
            Long roleId = Long.valueOf(role.getId());
            List<MenuDO> menuDOList = roleMapper.listMenu(roleId);
            List<Menu> menuList = menuDOList.stream().map(Menu::toMenu).collect(Collectors.toList());
            menuSet.addAll(menuList);
        }
        log.debug("指定用户所有菜单：" + menuSet);
        return menuSet;
    }
}
