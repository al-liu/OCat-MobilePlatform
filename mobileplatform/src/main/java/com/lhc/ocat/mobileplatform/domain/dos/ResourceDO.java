package com.lhc.ocat.mobileplatform.domain.dos;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author lhc
 * @date 2019-10-29 16:24
 */
@Data
@TableName(value = "resource")
public class ResourceDO {
    /**
     * 准发行状态
     */
    public static final Integer PRE_RELEASE_STATUS = 1;
    /**
     * 发行状态
     */
    public static final Integer RELEASE_STATUS = 2;

    private Long id;
    private Long applicationId;
    private String versionName;
    private Integer versionCode;
    private String data;
    private Date createTime;
    private Date updateTime;
    private Integer status;

}
