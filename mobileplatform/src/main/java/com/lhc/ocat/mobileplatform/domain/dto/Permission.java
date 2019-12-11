package com.lhc.ocat.mobileplatform.domain.dto;

import com.lhc.ocat.mobileplatform.domain.dos.PermissionDO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * @author lhc
 * @date 2019-11-23 23:26
 */
@Data
public class Permission {
    private String id;
    private String code;
    private String name;
    private String description;

    static public Permission toPermission(PermissionDO permissionDO) {
        Permission permission = new Permission();
        BeanUtils.copyProperties(permissionDO, permission);
        permission.setId(String.valueOf(permissionDO.getId()));
        return permission;
    }
}
