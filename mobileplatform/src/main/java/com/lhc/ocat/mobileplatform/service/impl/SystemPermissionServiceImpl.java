package com.lhc.ocat.mobileplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lhc.ocat.mobileplatform.domain.dos.PermissionDO;
import com.lhc.ocat.mobileplatform.domain.dto.Permission;
import com.lhc.ocat.mobileplatform.exception.ApiErrorType;
import com.lhc.ocat.mobileplatform.exception.ApiException;
import com.lhc.ocat.mobileplatform.mapper.PermissionMapper;
import com.lhc.ocat.mobileplatform.service.SystemPermissionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author lhc
 * @date 2019-11-27 22:57
 */
@Service
public class SystemPermissionServiceImpl implements SystemPermissionService {

    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public void createPermission(String code, String name, String description) throws ApiException {
        PermissionDO permissionDO = new PermissionDO();
        permissionDO.setCode(code);
        permissionDO.setName(name);
        permissionDO.setDescription(description);
        permissionMapper.insert(permissionDO);
    }

    @Override
    public void updatePermission(Long permissionId, String code, String name, String description) throws ApiException {
        PermissionDO permissionDO = permissionMapper.selectById(permissionId);
        if (Objects.isNull(permissionDO)) {
            throw new ApiException(ApiErrorType.SYSTEM_PERMISSION_NOT_FOUND_ERROR);
        }
        permissionDO.setName(name);
        permissionDO.setCode(code);
        permissionDO.setDescription(description);
        permissionMapper.updateById(permissionDO);
    }

    @Override
    public void deletePermission(Long permissionId) throws ApiException {
        PermissionDO permissionDO = permissionMapper.selectById(permissionId);
        if (Objects.isNull(permissionDO)) {
            throw new ApiException(ApiErrorType.SYSTEM_PERMISSION_NOT_FOUND_ERROR);
        }
        permissionMapper.deleteById(permissionId);
    }

    @Override
    public List<Permission> listPermission() throws ApiException {
        List<PermissionDO> permissionDOList = permissionMapper.selectList(
                new LambdaQueryWrapper<PermissionDO>()
                        .orderByDesc(PermissionDO::getCreateTime));
        List<Permission> permissionList = new ArrayList<>();
        for (PermissionDO permissionDO :
                permissionDOList) {
            Permission permission = Permission.toPermission(permissionDO);
            permissionList.add(permission);
        }
        return permissionList;
    }
}
