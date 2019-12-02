package com.lhc.ocat.mobileplatform.systemmanage;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lhc.ocat.mobileplatform.domain.dos.PermissionDO;
import com.lhc.ocat.mobileplatform.domain.dos.RoleDO;
import com.lhc.ocat.mobileplatform.domain.dos.UserDO;
import com.lhc.ocat.mobileplatform.mapper.RoleMapper;
import com.lhc.ocat.mobileplatform.mapper.UserMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author lhc
 * @date 2019-11-22 18:25
 */
@Log4j2
public class UserRealm extends AuthorizingRealm {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        Set<String> permissionSet = new HashSet<>();
        UserDO userDO = (UserDO) principals.getPrimaryPrincipal();
        List<RoleDO> roles = userMapper.listRoles(userDO.getId());
        Set<String> roleSet = roles.stream().map(RoleDO::getCode).collect(Collectors.toSet());
        for (RoleDO roleDO :
                roles) {
            log.debug("遍历 role:"+roleDO);
            List<PermissionDO> permissions = roleMapper.listPermissions(roleDO.getId());
            Set<String> codeSet = permissions.stream().map(PermissionDO::getCode).collect(Collectors.toSet());
            permissionSet.addAll(codeSet);
        }
        info.setRoles(roleSet);
        info.setStringPermissions(permissionSet);
        log.debug(String.format("%s 的所有角色:%s", userDO.getUsername(), roleSet));
        log.debug(String.format("%s 的所有权限:%s", userDO.getUsername(), permissionSet));
        return info;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String userName = (String) token.getPrincipal();
        if (Objects.isNull(userName)) {
            throw new AuthenticationException("token invalid.");
        }
        UserDO userDO = userMapper.selectOne(new LambdaQueryWrapper<UserDO>().eq(UserDO::getUsername, userName));
        if (Objects.isNull(userDO)) {
            throw new AuthenticationException("That username didn't existed.");
        }
        return new SimpleAuthenticationInfo(
                userDO,
                userDO.getPassword(),
                ByteSource.Util.bytes(userDO.getSalt()),
                getName());
    }
}
