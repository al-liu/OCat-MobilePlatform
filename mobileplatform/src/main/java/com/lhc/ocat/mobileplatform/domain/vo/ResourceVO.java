package com.lhc.ocat.mobileplatform.domain.vo;

import com.lhc.ocat.mobileplatform.domain.dos.ResourceDO;
import lombok.Data;

/**
 * @author lhc
 * @date 2019-11-26 15:02
 */
@Data
public class ResourceVO {
    private String id;
    private Integer status;
    private String versionName;
    private Integer versionCode;

    static public ResourceVO toResourceVO(ResourceDO resourceDO) {
        ResourceVO resourceVO = new ResourceVO();
        resourceVO.setVersionName(resourceDO.getVersionName());
        resourceVO.setVersionCode(resourceDO.getVersionCode());
        resourceVO.setId(String.valueOf(resourceDO.getId()));
        resourceVO.setStatus(resourceDO.getStatus());
        return resourceVO;
    }
}
