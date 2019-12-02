package com.lhc.ocat.mobileplatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lhc.ocat.mobileplatform.domain.dos.MenuDO;
import com.lhc.ocat.mobileplatform.domain.dos.PermissionDO;
import com.lhc.ocat.mobileplatform.domain.dos.RoleDO;

import java.util.List;

/**
 * @author lhc
 * @date 2019-11-23 23:58
 */
public interface RoleMapper extends BaseMapper<RoleDO> {

    /**
     * 查询指定角色的所有权限
     * @param roleId 角色的 id
     * @return 权限列表
     */
    List<PermissionDO> listPermissions(Long roleId);

    /**
     * 查询指定角色的所有菜单
     * @param roleId 角色id
     * @return 菜单列表
     */
    List<MenuDO> listMenu(Long roleId);

}
