package com.lhc.ocat.mobileplatform.domain.dto;

import com.lhc.ocat.mobileplatform.domain.dos.RoleDO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * @author lhc
 * @date 2019-11-23 23:25
 */
@Data
public class Role {
    private String id;
    private String code;
    private String name;
    private String description;

    static public Role toRole(RoleDO roleDO) {
        Role role = new Role();
        BeanUtils.copyProperties(roleDO, role);
        role.setId(String.valueOf(roleDO.getId()));
        return role;
    }
}
