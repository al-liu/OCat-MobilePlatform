package com.lhc.ocat.mobileplatform.service;

import com.lhc.ocat.mobileplatform.domain.dto.Resource;
import com.lhc.ocat.mobileplatform.domain.vo.ResourceVO;
import com.lhc.ocat.mobileplatform.exception.ApiException;
import com.lhc.ocat.mobileplatform.exception.BaseException;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author lhc
 * @date 2019-10-23 16:50
 */
public interface PublishPackageService {

    /**
     * 发布生产包，并生成差量包补丁。
     * @param packageFile 上传的生产包
     * @param versionName 版本号
     * @param versionCode 版本代号
     * @param appId 应用程序 appId
     * @param appSecret 应用程序 appSecret
     * @throws Exception 业务接口异常
     */
    void diffPublishPackage(MultipartFile packageFile,
                            String versionName,
                            int versionCode,
                            String appId,
                            String appSecret) throws Exception;

    /**
     * 发布新版本
     * @param applicationId 应用程序的 id
     * @param resourceId 版本资源的 id
     * @throws Exception 异常
     */
    void releaseNewVersion(Long applicationId, Long resourceId) throws Exception;

    /**
     * 删除待发行版本
     * @param applicationId 应用程序的 id
     * @param resourceId 版本资源 id
     * @throws Exception 异常
     */
    void removeResource(Long applicationId, Long resourceId) throws Exception;

    /**
     * 获取指定应用下的最新版本资源
     * @param applicationId 应用 id
     * @return ResourceVO
     */
    ResourceVO latestResource(Long applicationId);
}
