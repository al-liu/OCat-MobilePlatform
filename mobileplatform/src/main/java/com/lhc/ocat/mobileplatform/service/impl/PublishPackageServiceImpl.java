package com.lhc.ocat.mobileplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.io.Files;
import com.lhc.ocat.mobileplatform.domain.dos.ApplicationDO;
import com.lhc.ocat.mobileplatform.domain.dos.PatchDO;
import com.lhc.ocat.mobileplatform.domain.dos.ResourceDO;
import com.lhc.ocat.mobileplatform.domain.dto.Application;
import com.lhc.ocat.mobileplatform.domain.dto.Patch;
import com.lhc.ocat.mobileplatform.domain.dto.Resource;
import com.lhc.ocat.mobileplatform.domain.vo.ResourceVO;
import com.lhc.ocat.mobileplatform.exception.*;
import com.lhc.ocat.mobileplatform.mapper.ApplicationMapper;
import com.lhc.ocat.mobileplatform.mapper.PatchMapper;
import com.lhc.ocat.mobileplatform.mapper.ResourceMapper;
import com.lhc.ocat.mobileplatform.publishpackage.PublishPackage;
import com.lhc.ocat.mobileplatform.service.PublishPackageService;
import com.lhc.ocat.mobileplatform.publishpackage.DiffPackageUtil;
import net.lingala.zip4j.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author lhc
 * @date 2019-10-23 16:51
 */
@Service
public class PublishPackageServiceImpl implements PublishPackageService {

    private static Logger logger = LoggerFactory.getLogger(PublishPackageServiceImpl.class);

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    private PatchMapper patchMapper;

    @Autowired
    private PublishPackage publishPackage;

    @Autowired
    private ApplicationMapper applicationMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void diffPublishPackage(MultipartFile packageFile,
                                   String versionName,
                                   int versionCode,
                                   String appId,
                                   String appSecret) throws Exception {
        // 认证客户端身份
        ApplicationDO applicationDO = applicationMapper.selectOne(new LambdaQueryWrapper<ApplicationDO>()
                .eq(ApplicationDO::getAppId, appId));
        if (Objects.isNull(applicationDO)) {
            throw new ApiException(ApiErrorType.APP_NOT_FOUND_ERROR);
        }
        if (!Objects.equals(appSecret, applicationDO.getAppSecret())) {
            throw new ApiException(ApiErrorType.APP_SECRET_ERROR);
        }
        // 如果有未发行的包则不能再传新包
        List<ResourceDO> preReleaseList = resourceMapper.selectList(new LambdaQueryWrapper<ResourceDO>()
                .eq(ResourceDO::getStatus, ResourceDO.PRE_RELEASE_STATUS));
        if (preReleaseList.size() > 0) {
            throw new ApiException(ApiErrorType.APP_PRE_RELEASE_ERROR);
        }
        // 检查包的媒体类型
        if (!PublishPackage.PACKAGE_CONTENT_TYPE.equals(packageFile.getContentType())) {
            throw new ApiException(HttpErrorType.MEDIA_TYPE_NOT_SUPPORTED_ERROR,
                    "上传包媒体类型错误，应为 application/zip。");
        }
        // 查询包的版本号是否已存在
        ResourceDO existResourceDo = resourceMapper.selectOne(new LambdaQueryWrapper<ResourceDO>()
                .eq(ResourceDO::getVersionName, versionName));
        if (Objects.nonNull(existResourceDo)) {
            throw new ApiException(BusinessErrorType.ARGUMENT_DUPLICATION_ERROR,
                    String.format("versionName:%s 已存在，版本号不能重复。", versionName));
        }
        // 检查 versionCode 是否已存在或者小于已存在的 versionCode
        List<ResourceDO> geResourceDoList = resourceMapper.selectList(new LambdaQueryWrapper<ResourceDO>()
                .ge(ResourceDO::getVersionCode, versionCode));
        if (geResourceDoList.size() > 0) {
            throw new ApiException(HttpErrorType.ARGUMENT_NOT_VALID_ERROR,
                    String.format("versionCode:%s 已存在或者小于已存在的版本编号。",versionCode));
        }
        try {
            publishPackage.createPatchPackage(packageFile, versionName, versionCode, applicationDO);
        } catch (IOException e) {
            e.printStackTrace();
            // 处理 IOException
            SystemErrorType ioError = SystemErrorType.SYSTEM_IO_ERROR;
            ioError.setMessage(e.getMessage());
            throw new ApiException(ioError);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void releaseNewVersion(Long applicationId, Long resourceId) throws Exception {
        // 验证应用是否存在
        ApplicationDO applicationDO = applicationMapper.selectById(applicationId);
        if (Objects.isNull(applicationDO)) {
            throw new ApiException(ApiErrorType.APP_NOT_FOUND_ERROR);
        }
        // 验证资源是否存在
        ResourceDO resourceDO = resourceMapper.selectById(resourceId);
        if (Objects.isNull(resourceDO)) {
            throw new ApiException(ApiErrorType.RESOURCE_NOT_FOUND_ERROR);
        }
        // 验证资源是否是未发行的
        if (resourceDO.getStatus().equals(ResourceDO.RELEASE_STATUS)) {
            throw new ApiException(ApiErrorType.RESOURCE_RELEASE_ERROR);
        }
        resourceDO.setStatus(ResourceDO.RELEASE_STATUS);
        resourceMapper.updateById(resourceDO);
        // 更新补丁为可用
        patchMapper.enabledPatchs(applicationId, resourceDO.getVersionName());
        // 发布线上版本
        publishPackage.deployOnlinePackage(applicationDO.getAppId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeResource(Long applicationId, Long resourceId)  throws Exception {
        ApplicationDO applicationDO = applicationMapper.selectById(applicationId);
        if (Objects.isNull(applicationDO)) {
            throw new ApiException(ApiErrorType.APP_NOT_FOUND_ERROR);
        }
        ResourceDO resourceDO = resourceMapper.selectById(resourceId);
        if (Objects.isNull(resourceDO)) {
            throw new ApiException(ApiErrorType.RESOURCE_NOT_FOUND_ERROR);
        }
        if (resourceDO.getStatus().equals(ResourceDO.RELEASE_STATUS)) {
            throw new ApiException(ApiErrorType.RESOURCE_RELEASE_ERROR, "该版本已经发布，不能删除！");
        }
        // 删除本地包资源
        publishPackage.removeResource(Resource.toResource(resourceDO), applicationDO);
        // 删除数据库的资源信息
        patchMapper.delete(new LambdaQueryWrapper<PatchDO>().eq(PatchDO::getNewVersion, resourceDO.getVersionName()));
        resourceMapper.deleteById(resourceId);
    }

    @Override
    public ResourceVO latestResource(Long applicationId) {
        ApplicationDO applicationDO = applicationMapper.selectById(applicationId);
        if (Objects.isNull(applicationDO)) {
            throw new ApiException(ApiErrorType.APP_NOT_FOUND_ERROR);
        }
        Integer maxVersionCode = resourceMapper.getMaxVersionCode(applicationDO.getId());
        ResourceDO resourceDO = resourceMapper.selectOne(new LambdaQueryWrapper<ResourceDO>()
                .eq(ResourceDO::getVersionCode, maxVersionCode)
                .eq(ResourceDO::getStatus, ResourceDO.RELEASE_STATUS)
                .eq(ResourceDO::getApplicationId, applicationDO.getId()));
        return ResourceVO.toResourceVO(resourceDO);
    }
}
