package com.lhc.ocat.mobileplatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lhc.ocat.mobileplatform.domain.dos.RoleDO;
import com.lhc.ocat.mobileplatform.domain.dos.UserDO;

import java.util.List;

/**
 * @author lhc
 * @date 2019-11-23 23:56
 */
public interface UserMapper extends BaseMapper<UserDO> {

    /**
     * 查询指定用户的所有角色
     * @param userId 用户的 id
     * @return 返回角色列表
     */
    List<RoleDO> listRoles(Long userId);
}
