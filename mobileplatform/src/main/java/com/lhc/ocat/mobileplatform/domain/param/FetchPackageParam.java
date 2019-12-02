package com.lhc.ocat.mobileplatform.domain.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author lhc
 * @date 2019-11-05 11:20
 */
@Data
public class FetchPackageParam {

    @NotBlank(message = "versionName 不能为空")
    private String versionName;

    @NotBlank(message = "appId 不能为空")
    private String appId;

    @NotBlank(message = "appSecret 不能为空")
    private String appSecret;

}
