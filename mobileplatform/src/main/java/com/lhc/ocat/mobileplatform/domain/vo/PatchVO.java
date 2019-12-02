package com.lhc.ocat.mobileplatform.domain.vo;

import com.lhc.ocat.mobileplatform.domain.dos.PatchDO;
import lombok.Data;

/**
 * @author lhc
 * @date 2019-11-26 15:02
 */
@Data
public class PatchVO {
    private Long id;
    private Integer status;
    private String newVersion;
    private String oldVersion;
    private String url;

    static public PatchVO toPatchVO(PatchDO patchDO) {
        PatchVO patchVO = new PatchVO();
        patchVO.setNewVersion(patchDO.getNewVersion());
        patchVO.setOldVersion(patchDO.getOldVersion());
        patchVO.setUrl(patchDO.getUrl());
        patchVO.setId(patchDO.getId());
        patchVO.setStatus(patchDO.getStatus());
        return patchVO;
    }
}
