package com.lhc.ocat.mobileplatform.publishpackage;

import com.lhc.ocat.mobileplatform.domain.dos.ApplicationDO;
import com.lhc.ocat.mobileplatform.domain.dos.ResourceDO;
import com.lhc.ocat.mobileplatform.domain.dto.Patch;
import com.lhc.ocat.mobileplatform.domain.dto.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author lhc
 * @date 2019-11-01 11:53
 */
public interface PublishPackage {

    String PACKAGE_CONTENT_TYPE = "application/zip";
    String ZIP_FILE_SUFFIX = ".zip";
    String ALL_PACKAGE_NAME = "all.zip";

    /**
     * 用上传包生成全量包，差量包，部署到资源服务器，将 patch，resource 信息保存。
     * @param uploadPackage 用户上传包
     * @param versionName 版本号
     * @param versionCode 版本编码
     * @param applicationDO 资源包对应的应用程序
     * @throws Exception 异常
     */
    void createPatchPackage(MultipartFile uploadPackage,
                            String versionName,
                            int versionCode,
                            ApplicationDO applicationDO) throws Exception;

    /**
     * 获取最大要比较的旧版本 ResourceDO 列表
     * @param maxDiffCount 差量比较最大版本数
     * @param applicationDO 资源包对应的应用程序
     * @return ResourceDO列表
     */
    List<ResourceDO> listDiffOldVersion(String maxDiffCount, ApplicationDO applicationDO);

    /**
     * 保存包资源信息
     * @param resource 代表包资源的对象
     * @param applicationDO 资源包对应的应用程序
     */
    void savePackageResource(Resource resource, ApplicationDO applicationDO);

    /**
     * 保存补丁资源信息
     * @param patch 代表补丁资源的对象
     * @param applicationDO 资源包对应的应用程序
     */
    void savePackagePatch(Patch patch, ApplicationDO applicationDO);

    /**
     * 将补丁包（全量或差量）上传到资源服务器供用户下载
     * 可以放到 Web 服务器的静态资源目录下，也可以放到云存储服务器。
     * @param zipPackageFile 全量或差量的 zip 包
     * @param versionName 本次升级版本号
     * @param applicationDO 资源包对应的应用程序
     * @throws IOException IO异常
     * @return url 下载链接
     */
    String uploadPatchPackage(File zipPackageFile, String versionName, ApplicationDO applicationDO) throws IOException;

    /**
     * 部署准线上版本
     * 放到 Web 服务器静态资源目录下
     * @param versionFile 解压缩后的生产包
     * @param applicationDO 资源包对应的应用程序
     * @throws IOException IO异常
     */
    void deployPrepOnlinePackage(File versionFile, ApplicationDO applicationDO) throws IOException;

    /**
     * 部署线上版本，将预览版本代码复制到线上版本
     * @param appId 应用的 appId
     * @param versionName 上线的版本号
     * @throws IOException IO异常
     */
    void deployOnlinePackage(String appId) throws IOException;

    /**
     * 获取线上版本地址URL
     * @param appId 应用 appId
     * @return url 地址
     */
    String getOnlinePackageUrl(String appId);

    /**
     * 删除指定版本的本地资源
     * @param resource 版本资源
     * @param applicationDO 应用
     * @throws IOException IO异常
     */
    void removeResource(Resource resource, ApplicationDO applicationDO) throws IOException;

    /**
     * 删除指定应用下的所有本地资源
     * @param applicationDO 应用
     * @throws IOException IO异常
     */
    void removeAllResource(ApplicationDO applicationDO) throws IOException;
}
