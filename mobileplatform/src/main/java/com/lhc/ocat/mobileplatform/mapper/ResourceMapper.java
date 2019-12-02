package com.lhc.ocat.mobileplatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lhc.ocat.mobileplatform.domain.dos.ResourceDO;

/**
 * @author lhc
 * @date 2019-10-29 16:21
 */
public interface ResourceMapper extends BaseMapper<ResourceDO> {

    /**
     * 获取指定应用的最大的 VersionCode
     * @param applicationId 应用程序的 id
     * @return versionCode
     */
    Integer getMaxVersionCode(Long applicationId);
}
