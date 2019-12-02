package com.lhc.ocat.mobileplatform.publishpackage;

import com.lhc.ocat.mobileplatform.mapper.PatchMapper;
import com.lhc.ocat.mobileplatform.mapper.ResourceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lhc
 * @date 2019-11-01 16:37
 */

@Configuration
public class PublishPackageConfig {

    /**
     * 最大差量比较版本数量
     */
    @Value("${com.lhc.ocat.mobileplatform.max-diff-count}")
    private String maxDiffCount;
    /**
     * 差量比较工作空间路径
     */
    @Value("${com.lhc.ocat.mobileplatform.workshop-path}")
    private String workShopPath;
    /**
     * 包下载路径
     */
    @Value("${com.lhc.ocat.mobileplatform.package-download-path}")
    private String packageDownloadPath;

    /**
     * 线上部署路径
     */
    @Value("${com.lhc.ocat.mobileplatform.online-version-path}")
    private String onlineVersionPath;

    @Value("${com.lhc.ocat.mobileplatform.prep-online-version-path}")
    private String prepVersionPath;

    @Value("${com.lhc.ocat.mobileplatform.online-url}")
    private String onlinePath;

    @Autowired
    private ResourceMapper resourceMapper;
    @Autowired
    private PatchMapper patchMapper;

    @Bean
    public PublishPackage demoPublishPackage() {
        return new DemoPublishPackage(workShopPath,
                maxDiffCount,
                resourceMapper,
                patchMapper,
                packageDownloadPath,
                onlineVersionPath,
                prepVersionPath,
                onlinePath);
    }
}
