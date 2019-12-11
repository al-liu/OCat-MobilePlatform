package com.lhc.ocat.mobileplatform.publishpackage;

import com.lhc.ocat.mobileplatform.domain.dos.ApplicationDO;
import com.lhc.ocat.mobileplatform.domain.dos.ResourceDO;
import com.lhc.ocat.mobileplatform.domain.dto.Patch;
import com.lhc.ocat.mobileplatform.domain.dto.Resource;
import com.lhc.ocat.mobileplatform.exception.ApiErrorType;
import com.lhc.ocat.mobileplatform.exception.ApiException;
import com.lhc.ocat.mobileplatform.exception.SystemErrorType;
import lombok.extern.log4j.Log4j2;
import net.lingala.zip4j.ZipFile;
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
 * @date 2019-11-01 15:26
 */
@Log4j2
public abstract class AbstractPublishPackage implements PublishPackage {

    private String workshopPath;
    private String maxDiffCount;
    private static final int WORKSHOP_INIT_FILE_COUNT = 2;

    AbstractPublishPackage(String workshopPath, String maxDiffCount) {
        this.workshopPath = workshopPath;
        this.maxDiffCount = maxDiffCount;
    }

    @Override
    public void createPatchPackage(MultipartFile uploadPackage,
                                   String versionName,
                                   int versionCode,
                                   ApplicationDO applicationDO)  throws Exception {
        // 如果 workshop 目录存在则先删除
        File workshopFile = new File(workshopPath);
        if (workshopFile.exists()) {
            DiffPackageUtil.cleanFiles(workshopFile);
        }
        // 将上传文件移动到指定目录 workshop 并解压包。
        String allZipPackagePath = Paths.get(workshopPath, ALL_PACKAGE_NAME).toString();
        File allZipPackageFile = new File(allZipPackagePath);
        DiffPackageUtil.unzipUploadPackage(uploadPackage, allZipPackageFile);
        // 将解压后的包文件命名成 versionName
        if (workshopFile.exists()) {
            File[] files = workshopFile.listFiles();
            if (Objects.nonNull(files)) {
                String uploadPackageName = "";
                // 找到解压后的文件夹名
                if (files.length == WORKSHOP_INIT_FILE_COUNT) {
                    for (File file :
                            files) {
                        if (!file.getName().contains(ZIP_FILE_SUFFIX)) {
                            uploadPackageName = file.getName();
                        }
                    }
                } else {
                    // 应该只有两个文件
                    log.error("workshop 初始解压后的文件数不为 2");
                    throw new ApiException(SystemErrorType.SYSTEM_ERROR, "差量包工作异常");
                }
                if (!uploadPackageName.equals(versionName)) {
                    log.debug("开始改名包文件");
                    String uploadFilePath = Paths.get(workshopPath, uploadPackageName).toString();
                    String renameFilePath = Paths.get(workshopPath, versionName).toString();
                    File uploadFile = new File(uploadFilePath);
                    File renameFile = new File(renameFilePath);
                    boolean renameResult = uploadFile.renameTo(renameFile);
                    if (!renameResult) {
                        log.error("差量包重命名初始的解压包失败");
                    }
                    // 删除旧 all.zip 包，压缩新 all.zip 包
                    boolean deleteResult = allZipPackageFile.delete();
                    if (!deleteResult) {
                        log.error("差量包删除初始的 all.zip 解压包失败");
                    }
                    new ZipFile(allZipPackageFile).addFolder(renameFile);
                }// if end：如果解压后的文件夹名与上送版本号一样则不用做重命名和重打压缩包的操作
            } else {
                // 工作目录下为有响应文件
                log.error("workshop 初始解压后的 file[] 为 null");
                throw new ApiException(SystemErrorType.SYSTEM_ERROR, "差量包工作异常");
            }
        }
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
