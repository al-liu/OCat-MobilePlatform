package com.lhc.ocat.mobileplatform.domain.dos;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author lhc
 * @date 2019-11-23 22:54
 */
@Data
@TableName(value = "user")
public class UserDO {
    public static final Integer ENABLED = 1;
    public static final Integer DISABLED = 2;
    private Long id;
    private String username;
    private String password;
    private String description;
    private Integer enabled;
    private String salt;
    private Date createTime;
    private Date updateTime;
}
