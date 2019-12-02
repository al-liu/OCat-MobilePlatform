package com.lhc.ocat.mobileplatform.domain.dto;

import lombok.Data;

/**
 * @author lhc
 * @date 2019-11-23 23:34
 */
@Data
public class Menu {
    private Long id;
    private Long parentId;
    private Integer orderNum;
    private Integer type;
    private String name;
    private String icon;
    private String href;
    private String description;
}
