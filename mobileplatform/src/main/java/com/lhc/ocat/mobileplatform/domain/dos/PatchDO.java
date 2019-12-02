package com.lhc.ocat.mobileplatform.domain.dos;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author lhc
 * @date 2019-10-29 16:39
 */
@Data
@TableName(value = "patch")
public class PatchDO {
    /**
     * 不可用状态
     */
    public static final Integer DISABLED_STATUS = 1;
    /**
     * 可用状态
     */
    public static final Integer ENABLED_STATUS = 2;

    private Long id;
    private String newVersion;
    private String oldVersion;
    private String url;
    private String data;
    private Long applicationId;
    private Date createTime;
    private Date updateTime;
    private Integer status;

}
