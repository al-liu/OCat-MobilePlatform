package com.lhc.ocat.mobileplatform.service;

import com.lhc.ocat.mobileplatform.domain.dto.Patch;
import com.lhc.ocat.mobileplatform.domain.vo.PatchPackageVO;
import com.lhc.ocat.mobileplatform.exception.ApiException;

/**
 * @author lhc
 * @date 2019-11-05 11:21
 */
public interface FetchPackageService {

    /**
     * 获取指定版本号的补丁信息
     * @param appId 应用程序的 appId
     * @param appSecret 应用程序的 appSecret
     * @param versionName 当前客户端版本号
     * @return 补丁信息
     * @throws ApiException 业务接口异常
     */
    PatchPackageVO fetchPackagePatch(String appId,
                                     String appSecret,
                                     String versionName) throws ApiException;


}
