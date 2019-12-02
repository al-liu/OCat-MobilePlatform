package com.lhc.ocat.mobileplatform.domain.vo;

import com.lhc.ocat.mobileplatform.domain.dto.Patch;
import com.lhc.ocat.mobileplatform.domain.dto.Resource;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author lhc
 * @date 2019-11-30 10:52
 */
@Data
@Builder
public class PatchPackageVO {
    private String newVersion;
    private String oldVersion;
    private String downloadUrl;
    private String onlineUrl;
    private List<String> changeResourceInfo;
    private List<String> removeResourceInfo;

    public static PatchPackageVO toPatchPackageVO(Patch patch, String onlineUrl) {
        if (Objects.isNull(patch)) {
            return null;
        }
        List<String> changeInfo = new ArrayList<>();
        List<String> removeInfo = new ArrayList<>();
        if (Objects.nonNull(patch.getChangeResources())) {
            changeInfo = patch.getChangeResources().stream().map(Resource::getPath).collect(Collectors.toList());
        }
        if (Objects.nonNull(patch.getRemoveResources())) {
            removeInfo = patch.getRemoveResources().stream().map(Resource::getPath).collect(Collectors.toList());;
        }
        String downloadUrl = patch.getUrl();
        return PatchPackageVO.builder()
                    .newVersion(patch.getNewVersion())
                    .oldVersion(patch.getOldVersion())
                    .downloadUrl(downloadUrl)
                    .onlineUrl(onlineUrl)
                    .changeResourceInfo(changeInfo)
                    .removeResourceInfo(removeInfo).build();
    }
}
