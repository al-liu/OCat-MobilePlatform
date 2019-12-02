package com.lhc.ocat.mobileplatform.domain.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author lhc
 * @date 2019-11-26 14:15
 */
@Data
public class ReleasePackageParam {
    @NotNull(message = "applicationId 不能为空")
    private Long applicationId;
    @NotNull(message = "resourceId 不能为空")
    private Long resourceId;
}
