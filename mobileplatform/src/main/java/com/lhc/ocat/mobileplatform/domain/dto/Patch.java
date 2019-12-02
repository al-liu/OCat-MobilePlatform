package com.lhc.ocat.mobileplatform.domain.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lhc.ocat.mobileplatform.domain.dos.PatchDO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.IOException;
import java.util.List;

/**
 * @author lhc
 * @date 2019-10-23 13:48
 */
@Getter
@Setter
@ToString
public class Patch {

    /**
     * 新版本
     */
    private String newVersion;
    /**
     * 旧版本
     */
    private String oldVersion;

    /**
     * merge patch 信息，删除资源
     */
    private List<Resource> removeResources;

    /**
     * merge patch 信息，变更资源
     */
    private List<Resource> changeResources;

    /**
     * 补丁包下载地址
     */
    private String url;

    static public PatchDO toPatchDO(Patch patch) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String data = objectMapper.writeValueAsString(patch);
            PatchDO patchDO = new PatchDO();
            patchDO.setNewVersion(patch.newVersion);
            patchDO.setOldVersion(patch.oldVersion);
            patchDO.setUrl(patch.getUrl());
            patchDO.setData(data);
            return patchDO;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    static public Patch toPatch(PatchDO patchDO) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(patchDO.getData(), Patch.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
