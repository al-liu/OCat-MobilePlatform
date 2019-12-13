package com.lhc.ocat.mobileplatform.publishpackage;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.io.Files;
import com.lhc.ocat.mobileplatform.domain.dos.ApplicationDO;
import com.lhc.ocat.mobileplatform.domain.dos.PatchDO;
import com.lhc.ocat.mobileplatform.domain.dos.ResourceDO;
import com.lhc.ocat.mobileplatform.domain.dto.Application;
import com.lhc.ocat.mobileplatform.domain.dto.Patch;
import com.lhc.ocat.mobileplatform.domain.dto.Resource;
import com.lhc.ocat.mobileplatform.mapper.PatchMapper;
import com.lhc.ocat.mobileplatform.mapper.ResourceMapper;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

/**
 * @author lhc
 * @date 2019-11-01 16:17
 */
@Log4j2
public class DemoPublishPackage extends AbstractPublishPackage {

    private static final Logger logger = LoggerFactory.getLogger(DemoPublishPackage.class);

    private ResourceMapper resourceMapper;
    private PatchMapper patchMapper;
    private String downloadPath;
    private String onlinePath;
    private String prepPath;
    private String onlineUrl;

    DemoPublishPackage(String workshopPath,
                       String maxDiffCount,
                       ResourceMapper resourceMapper,
                       PatchMapper patchMapper,
                       String downloadPath,
                       String onlinePath,
                       String prepPath,
                       String onlineUrl) {
        super(workshopPath, maxDiffCount);
        this.resourceMapper = resourceMapper;
        this.patchMapper = patchMapper;
        this.downloadPath = downloadPath;
        this.onlinePath = onlinePath;
        this.prepPath = prepPath;
        this.onlineUrl = onlineUrl;
    }

    @Override
    public List<ResourceDO> listDiffOldVersion(String maxDiffCount, ApplicationDO applicationDO) {
        String limitSql = String.format("limit %s", maxDiffCount);
        return resourceMapper.selectList(new LambdaQueryWrapper<ResourceDO>()
                .eq(ResourceDO::getApplicationId, applicationDO.getId())
                .orderByDesc(ResourceDO::getVersionCode).last(limitSql));
    }

    @Override
    public void savePackageResource(Resource resource, ApplicationDO applicationDO) {
        ResourceDO resourceDO = Resource.toResourceDO(resource);
        if (Objects.nonNull(resourceDO)) {
            resourceDO.setApplicationId(applicationDO.getId());
            resourceMapper.insert(resourceDO);
        } else {
            logger.error("保存资源包对象失败！");
        }
    }

    @Override
    public void savePackagePatch(Patch patch, ApplicationDO applicationDO) {
        PatchDO patchDO = Patch.toPatchDO(patch);
        if (Objects.nonNull(patchDO)) {
            patchDO.setApplicationId(applicationDO.getId());
            patchMapper.insert(patchDO);
        } else {
            logger.error("保存补丁包对象失败！");
        }
    }

    @Override
    public String uploadPatchPackage(File zipPackageFile, String versionName, ApplicationDO applicationDO) throws IOException {
        String fileName = zipPackageFile.getName();
        String toMovePath = Paths.get(downloadPath, applicationDO.getAppId(),versionName, fileName).toString();
        File toMoveFile = new File(toMovePath);
        if (!toMoveFile.getParentFile().exists()) {
            toMoveFile.getParentFile().mkdirs();
        }
        Files.move(zipPackageFile, toMoveFile);
        return "http://192.168.1.104:8080/"
                + "download/packages/"
                + applicationDO.getAppId()
                + "/" + versionName
                + "/" + fileName;
    }

    @Override
    public void deployPrepOnlinePackage(File versionFile, ApplicationDO applicationDO) {
        String previewFilePath = Paths.get(prepPath, applicationDO.getAppId()).toString();
        File previewFile = new File(previewFilePath);
        if (previewFile.exists()) {
            DiffPackageUtil.cleanFiles(previewFile);
        }
        DiffPackageUtil.copyFiles(versionFile.getPath(), previewFilePath);
    }

    @Override
    public void deployOnlinePackage(String appId) {
        String onlineFilePath = Paths.get(onlinePath, appId).toString();
        File onlineFile = new File(onlineFilePath);
        if (onlineFile.exists()) {
            DiffPackageUtil.cleanFiles(onlineFile);
        }
        String previewFilePath = Paths.get(prepPath, appId).toString();
        DiffPackageUtil.copyFiles(previewFilePath, onlineFilePath);
    }

    @Override
    public String getOnlinePackageUrl(String appId) {
        return onlineUrl;
    }

    @Override
    public void removeResource(Resource resource, ApplicationDO applicationDO) {
        String previewFilePath = Paths.get(prepPath, applicationDO.getAppId()).toString();
        String patchFilePatch = Paths.get(downloadPath, applicationDO.getAppId(),resource.getVersionName()).toString();
        DiffPackageUtil.cleanFiles(new File(previewFilePath));
        DiffPackageUtil.cleanFiles(new File(patchFilePatch));
    }

    @Override
    public void removeAllResource(ApplicationDO applicationDO) {
        String previewFilePath = Paths.get(prepPath, applicationDO.getAppId()).toString();
        String onlineFilePath = Paths.get(onlinePath, applicationDO.getAppId()).toString();
        String patchFilePatch = Paths.get(downloadPath, applicationDO.getAppId()).toString();
        DiffPackageUtil.cleanFiles(new File(previewFilePath));
        DiffPackageUtil.cleanFiles(new File(onlineFilePath));
        DiffPackageUtil.cleanFiles(new File(patchFilePatch));
    }

}
