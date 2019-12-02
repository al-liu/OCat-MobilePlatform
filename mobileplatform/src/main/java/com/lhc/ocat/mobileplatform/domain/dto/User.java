package com.lhc.ocat.mobileplatform.domain.dto;

import com.lhc.ocat.mobileplatform.domain.dos.UserDO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * @author lhc
 * @date 2019-11-23 23:24
 */
@Data
public class User {
    private Long id;
    private String username;
    private String description;
    private Integer enabled;
}
