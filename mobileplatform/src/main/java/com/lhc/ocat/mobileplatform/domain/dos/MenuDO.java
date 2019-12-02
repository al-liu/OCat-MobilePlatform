package com.lhc.ocat.mobileplatform.domain.dos;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author lhc
 * @date 2019-11-23 23:31
 */
@Data
@TableName(value = "menu")
public class MenuDO {
    private Long id;
    private Long parentId;
    private Integer orderNum;
    private Integer type;
    private String name;
    private String icon;
    private String href;
    private String description;
    private Date createTime;
    private Date updateTime;
}
