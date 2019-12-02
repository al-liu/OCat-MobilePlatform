package com.lhc.ocat.mobileplatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lhc.ocat.mobileplatform.domain.dos.PatchDO;

/**
 * @author lhc
 * @date 2019-10-29 16:22
 */
public interface PatchMapper extends BaseMapper<PatchDO> {

    /**
     * 设置该 applicationId 应用程序下的所有 new_version 为 versionName 的补丁为可用
     * @param applicationId 应用程序 id
     * @param versionName 补丁的新版本号 new_version
     */
    void enabledPatchs(Long applicationId, String versionName);
}
