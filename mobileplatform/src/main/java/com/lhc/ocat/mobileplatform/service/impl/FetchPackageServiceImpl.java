package com.lhc.ocat.mobileplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lhc.ocat.mobileplatform.domain.dos.ApplicationDO;
import com.lhc.ocat.mobileplatform.domain.dos.PatchDO;
import com.lhc.ocat.mobileplatform.domain.dos.ResourceDO;
import com.lhc.ocat.mobileplatform.domain.dto.Patch;
import com.lhc.ocat.mobileplatform.domain.vo.PatchPackageVO;
import com.lhc.ocat.mobileplatform.exception.ApiErrorType;
import com.lhc.ocat.mobileplatform.exception.ApiException;
import com.lhc.ocat.mobileplatform.exception.HttpErrorType;
import com.lhc.ocat.mobileplatform.mapper.ApplicationMapper;
import com.lhc.ocat.mobileplatform.mapper.PatchMapper;
import com.lhc.ocat.mobileplatform.mapper.ResourceMapper;
import com.lhc.ocat.mobileplatform.publishpackage.PublishPackage;
import com.lhc.ocat.mobileplatform.service.FetchPackageService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/**
 * @author lhc
 * @date 2019-11-05 11:22
 */
@Log4j2
@Service
public class FetchPackageServiceImpl implements FetchPackageService {

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    private PatchMapper patchMapper;

    @Autowired
    private ApplicationMapper applicationMapper;

    @Autowired
    private PublishPackage publishPackage;

    @Override
    public PatchPackageVO fetchPackagePatch(String appId,
                                   String appSecret,
                                   String versionName) throws ApiException {
        // 客户端身份认证
        // 认证客户端身份
        ApplicationDO applicationDO = applicationMapper.selectOne(new LambdaQueryWrapper<ApplicationDO>()
                .eq(ApplicationDO::getAppId, appId));
        if (Objects.isNull(applicationDO)) {
            throw new ApiException(ApiErrorType.APP_NOT_FOUND_ERROR);
        }
        if (!Objects.equals(appSecret, applicationDO.getAppSecret())) {
            throw new ApiException(ApiErrorType.APP_SECRET_ERROR);
        }
        ResourceDO oldResource = resourceMapper.selectOne(new LambdaQueryWrapper<ResourceDO>()
                .eq(ResourceDO::getVersionName, versionName));
        if (Objects.isNull(oldResource)) {
            throw new ApiException(ApiErrorType.RESOURCE_NOT_FOUND_ERROR);
        }
        if (oldResource.getStatus().equals(ResourceDO.PRE_RELEASE_STATUS)) {
            throw new ApiException(ApiErrorType.RESOURCE_DISENABLE_ERROR);
        }
        // versionName 为客户端 h5 的最新版本
        Integer maxVersionCode = resourceMapper.getMaxVersionCode(applicationDO.getId());
        log.debug("maxVersionCode:" + maxVersionCode);
        // 两个 eq 需要测试
        ResourceDO resourceDO = resourceMapper.selectOne(new LambdaQueryWrapper<ResourceDO>()
                .eq(ResourceDO::getVersionCode, maxVersionCode)
                .eq(ResourceDO::getStatus, ResourceDO.RELEASE_STATUS)
                .eq(ResourceDO::getApplicationId, applicationDO.getId()));
        if (Objects.isNull(resourceDO)) {
            log.debug("未找到可用的版本资源！！");
            throw new ApiException(ApiErrorType.RESOURCE_ENABLE_NOT_FOUND_ERROR);
        }
        String newVersionName = resourceDO.getVersionName();
        String onlineUrl = publishPackage.getOnlinePackageUrl(applicationDO.getAppId());
        // 找到对应的差量包，先查询当前最新的版本号 versionName，然后用新旧版本号查询对应的补丁
        if (!newVersionName.equals(versionName)) {
            PatchDO patchDO = patchMapper.selectOne(
                    new LambdaQueryWrapper<PatchDO>()
                            .eq(PatchDO::getStatus, PatchDO.ENABLED_STATUS)
                            .eq(PatchDO::getNewVersion, newVersionName)
                            .eq(PatchDO::getOldVersion, versionName));
            if (Objects.nonNull(patchDO)) {
                // 差量补丁包
                Patch diffPatch = Patch.toPatch(patchDO);
                return PatchPackageVO.toPatchPackageVO(diffPatch, onlineUrl);
            }
        }
        // 全量包
        PatchDO allPatchDO = patchMapper.selectOne(
                new LambdaQueryWrapper<PatchDO>()
                        .eq(PatchDO::getStatus, PatchDO.ENABLED_STATUS)
                        .eq(PatchDO::getNewVersion, newVersionName)
                        .eq(PatchDO::getOldVersion, newVersionName));
        Patch allPatch = Patch.toPatch(allPatchDO);
        return PatchPackageVO.toPatchPackageVO(allPatch, onlineUrl);
    }
}
