package com.lhc.ocat.mobileplatform.domain.param.system;

import lombok.Data;

/**
 * @author lhc
 * @date 2019-11-27 18:00
 */
@Data
public class MenuParam {
    private Long parentId;
    private Integer orderNum;
    private Integer type;
    private String name;
    private String icon;
    private String href;
    private String description;
}
