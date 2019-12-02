package com.lhc.ocat.mobileplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lhc.ocat.mobileplatform.domain.dos.RoleDO;
import com.lhc.ocat.mobileplatform.domain.dos.UserDO;
import com.lhc.ocat.mobileplatform.domain.dos.UserRoleDO;
import com.lhc.ocat.mobileplatform.domain.dto.Role;
import com.lhc.ocat.mobileplatform.domain.dto.User;
import com.lhc.ocat.mobileplatform.exception.ApiErrorType;
import com.lhc.ocat.mobileplatform.exception.ApiException;
import com.lhc.ocat.mobileplatform.mapper.RoleMapper;
import com.lhc.ocat.mobileplatform.mapper.UserMapper;
import com.lhc.ocat.mobileplatform.mapper.UserRoleMapper;
import com.lhc.ocat.mobileplatform.service.SystemUserService;
import com.lhc.ocat.mobileplatform.systemmanage.ShiroConfig;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author lhc
 * @date 2019-11-27 19:45
 */
@Service
public class SystemUserServiceImpl implements SystemUserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;

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
        userMapper.deleteById(userDO.getId());
    }

    @Override
    public List<User> listUser() throws ApiException {
        List<UserDO> list = userMapper.selectList(new LambdaQueryWrapper<UserDO>().orderByDesc(UserDO::getCreateTime));
        List<User> userList = new ArrayList<>();
        for (UserDO userDO:
             list) {
            User user = new User();
            BeanUtils.copyProperties(userDO, user);
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
            Role role = new Role();
            BeanUtils.copyProperties(roleDO, role);
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
}
