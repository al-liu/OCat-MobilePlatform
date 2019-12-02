package com.lhc.ocat.mobileplatform.domain.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author lhc
 * @date 2019-10-23 16:15
 */
@Data
public class UploadPackageParam {

    @NotBlank(message = "versionName 不能为空")
    private String versionName;

    @NotNull(message = "versionCode 不能为空")
    private Integer versionCode;

    @NotBlank(message = "appId 不能为空")
    private String appId;

    @NotBlank(message = "appSecret 不能为空")
    private String appSecret;

}
