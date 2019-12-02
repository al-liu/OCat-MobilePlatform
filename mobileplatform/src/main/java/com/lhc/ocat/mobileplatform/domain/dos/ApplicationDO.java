package com.lhc.ocat.mobileplatform.domain.dos;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author lhc
 * @date 2019-11-14 18:23
 */
@Data
@TableName(value = "application")
public class ApplicationDO {

    private Long id;
    private String name;
    private String description;
    private String appId;
    private String appSecret;
    private Date createTime;
    private Date updateTime;

}
