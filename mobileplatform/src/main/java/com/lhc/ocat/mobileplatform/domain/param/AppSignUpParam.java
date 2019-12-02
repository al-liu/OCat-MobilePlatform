package com.lhc.ocat.mobileplatform.domain.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author lhc
 * @date 2019-11-15 13:33
 */
@Data
public class AppSignUpParam {

    @NotBlank(message = "应用名称不能为空")
    private String name;

    @NotBlank(message = "应用描述不能为空")
    private String description;

}
