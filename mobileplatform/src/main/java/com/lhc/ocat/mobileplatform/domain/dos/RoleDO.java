package com.lhc.ocat.mobileplatform.domain.dos;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author lhc
 * @date 2019-11-23 23:08
 */
@Data
@TableName(value = "role")
public class RoleDO {
    private Long id;
    private String code;
    private String name;
    private String description;
    private Date createTime;
    private Date updateTime;
}
