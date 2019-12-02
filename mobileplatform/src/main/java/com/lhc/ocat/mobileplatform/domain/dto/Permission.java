package com.lhc.ocat.mobileplatform.domain.dto;

import lombok.Data;

/**
 * @author lhc
 * @date 2019-11-23 23:26
 */
@Data
public class Permission {
    private Long id;
    private String code;
    private String name;
    private String description;
}
