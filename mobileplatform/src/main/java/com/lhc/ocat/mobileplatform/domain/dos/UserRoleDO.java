package com.lhc.ocat.mobileplatform.domain.dos;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author lhc
 * @date 2019-11-29 18:28
 */
@TableName(value = "user_role")
@Data
public class UserRoleDO {
    private Long id;
    private Long userId;
    private Long roleId;
}
