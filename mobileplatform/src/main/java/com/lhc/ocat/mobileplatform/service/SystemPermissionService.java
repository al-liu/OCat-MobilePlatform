package com.lhc.ocat.mobileplatform.service;

import com.lhc.ocat.mobileplatform.domain.dto.Permission;
import com.lhc.ocat.mobileplatform.exception.ApiException;

import java.util.List;

/**
 * @author lhc
 * @date 2019-11-27 22:52
 */
public interface SystemPermissionService {

    /**
     * 创建权限
     * @param code 权限编号
     * @param name 权限名称
     * @param description 权限描述
     * @throws ApiException 业务异常
     */
    void createPermission(String code, String name, String description) throws ApiException;

    /**
     * 修改权限
     * @param permissionId 权限id
     * @param code 编号
     * @param name 名称
     * @param description 描述
     * @throws ApiException 业务异常
     */
    void updatePermission(Long permissionId, String code, String name, String description) throws ApiException;

    /**
     * 删除权限
     * @param permissionId 权限id
     * @throws ApiException 业务异常
     */
    void deletePermission(Long permissionId) throws ApiException;

    /**
     * 查询所有权限
     * @return 权限列表
     * @throws ApiException 业务异常
     */
    List<Permission> listPermission() throws ApiException;
}
