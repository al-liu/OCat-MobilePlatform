package com.lhc.ocat.mobileplatform.publishpackage;

import com.lhc.ocat.mobileplatform.domain.dos.ApplicationDO;
import com.lhc.ocat.mobileplatform.domain.dos.ResourceDO;
import com.lhc.ocat.mobileplatform.domain.dto.Patch;
import com.lhc.ocat.mobileplatform.domain.dto.Resource;
import net.lingala.zip4j.ZipFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lhc
 * @date 2019-11-01 15:26
 */
public abstract class AbstractPublishPackage implements PublishPackage {

    private String workshopPath;
    private String maxDiffCount;

    AbstractPublishPackage(String workshopPath, String maxDiffCount) {
        this.workshopPath = workshopPath;
        this.maxDiffCount = maxDiffCount;
    }

    @Override
    public void createPatchPackage(MultipartFile uploadPackage,
                                   String versionName,
                                   int versionCode,
                                   ApplicationDO applicationDO)  throws IOException {
        // 将上传文件移动到指定目录 workshop 并解压包。
        String allZipPackagePath = Paths.get(workshopPath, ALL_PACKAGE_NAME).toString();
        File allZipPackageFile = new File(allZipPackagePath);
        DiffPackageUtil.unzipUploadPackage(uploadPackage, allZipPackageFile);
        // 解压后包目录
        String allUnzipPackagePath = Paths.get(workshopPath, versionName).toString();
        File allUnzipPackageFile = new File(allUnzipPackagePath);
        // 递归包目录，转成 Resource 对象
        Resource resource = new Resource();
        resource.setVersionName(versionName);
        resource.setVersionCode(versionCode);
        resource.setName("./");
        resource.setPath("./");
        resource.setResources(new ArrayList<>());
        DiffPackageUtil.drawResourceMap(allUnzipPackageFile.listFiles(),
                allUnzipPackageFile.getPath(),
                resource.getResources());
        // 获得比较旧版本做差量比较
        List<ResourceDO> oldResourceDOList = listDiffOldVersion(maxDiffCount, applicationDO);
        if (oldResourceDOList.size() > 0) {
            List<Resource> oldResourceList = oldResourceDOList.stream()
                    .map(Resource::toResource).collect(Collectors.toList());
            for (Resource oldResource:
                    oldResourceList) {
                // 比较新旧版本差异，将比对信息转成 patch 对象。
                List<Resource> changeResourceList = new ArrayList<>();
                List<Resource> removeResourceList = new ArrayList<>();
                Patch patch = new Patch();
                patch.setNewVersion(resource.getVersionName());
                patch.setOldVersion(oldResource.getVersionName());
                patch.setChangeResources(changeResourceList);
                patch.setRemoveResources(removeResourceList);
                DiffPackageUtil.diffResources(resource.getResources(),
                        oldResource.getResources(),
                        patch.getChangeResources(),
                        patch.getRemoveResources());
                // 命名差量包
                String patchName = versionName + "_" + oldResource.getVersionName();
                String patchFileName = patchName + ZIP_FILE_SUFFIX;
                String toPatchPath = Paths.get(workshopPath, patchName).toString();
                // 根据比较后的 patch 对象生成差量版本文件
                DiffPackageUtil.createPatchPackage(allUnzipPackagePath, toPatchPath, patch.getChangeResources());
                // 将差量版本文件打成压缩包
                File patchFile = new File(toPatchPath);
                String zipPatchPath = Paths.get(workshopPath, patchFileName).toString();
                new ZipFile(zipPatchPath).addFolder(patchFile);
                // 将差量压缩包上传到下载服务器，并生成下载链接。
                File fromMoveFile = new File(zipPatchPath);
                String url = uploadPatchPackage(fromMoveFile, versionName, applicationDO);
                // 将 patch 对象存入数据库
                patch.setUrl(url);
                savePackagePatch(patch, applicationDO);
            }
        }
        // 保存新版本 Resource 信息，上传全量包，生成并保存全量补丁信息。
        savePackageResource(resource, applicationDO);
        String url = uploadPatchPackage(allZipPackageFile, versionName, applicationDO);
        Patch patch = new Patch();
        patch.setNewVersion(resource.getVersionName());
        patch.setOldVersion(resource.getVersionName());
        patch.setUrl(url);
        savePackagePatch(patch, applicationDO);
        // 部署线上版本
        deployPrepOnlinePackage(allUnzipPackageFile, applicationDO);
        // 清理操作目录
        DiffPackageUtil.cleanFiles(new File(workshopPath));
    }
}
