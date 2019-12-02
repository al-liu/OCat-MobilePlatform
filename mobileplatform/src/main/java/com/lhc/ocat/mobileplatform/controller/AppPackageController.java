package com.lhc.ocat.mobileplatform.controller;

import com.lhc.ocat.mobileplatform.domain.dto.Patch;
import com.lhc.ocat.mobileplatform.domain.param.FetchPackageParam;
import com.lhc.ocat.mobileplatform.domain.param.ReleasePackageParam;
import com.lhc.ocat.mobileplatform.domain.param.UploadPackageParam;
import com.lhc.ocat.mobileplatform.domain.vo.PatchPackageVO;
import com.lhc.ocat.mobileplatform.domain.vo.Result;
import com.lhc.ocat.mobileplatform.service.FetchPackageService;
import com.lhc.ocat.mobileplatform.service.PublishPackageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author lhc
 * @date 2019-11-05 11:15
 */
@Validated
@RestController
@RequestMapping(value="/package")
public class AppPackageController {

    private static Logger logger = LoggerFactory.getLogger(AppPackageController.class);

    @Autowired
    private PublishPackageService publishPackageService;
    @Autowired
    private FetchPackageService fetchPackageService;

    /**
     * app 前端包的发布
     * @param file 包文件
     * @param packageParam 发布参数（版本号，版本序号）
     * @return 返回结果
     */
    @PostMapping("/publish")
    public Result uploadPackage(@RequestParam("package") MultipartFile file,@Validated UploadPackageParam packageParam) {
        publishPackageService.diffPublishPackage(file,
                packageParam.getVersionName(),
                packageParam.getVersionCode(),
                packageParam.getAppId(),
                packageParam.getAppSecret());
        return Result.success();
    }

    /**
     * 查询差量补丁包
     * @param packageParam 版本信息
     * @return 返回补丁信息
     */
    @PostMapping("/fetch")
    public Result fetchPackage(@RequestBody @Validated FetchPackageParam packageParam) {
        PatchPackageVO patch = fetchPackageService.fetchPackagePatch(packageParam.getAppId(),
                packageParam.getAppSecret(),
                packageParam.getVersionName());
        return Result.success(patch);
    }

    @PostMapping("/release")
    public Result releasePackage(@RequestBody @Validated ReleasePackageParam packageParam) {
        publishPackageService.releaseNewVersion(packageParam.getApplicationId(), packageParam.getResourceId());
        return Result.success();
    }

}
