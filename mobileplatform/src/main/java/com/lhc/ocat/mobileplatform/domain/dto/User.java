package com.lhc.ocat.mobileplatform.domain.dto;

import com.lhc.ocat.mobileplatform.domain.dos.UserDO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author lhc
 * @date 2019-11-23 23:24
 */
@Data
public class User {
    private String id;
    private String username;
    private String description;
    private Integer enabled;
    private Boolean lock;
    private List<Menu> menuList;
    private List<Menu> buttonList;

    static public User toUser(UserDO userDO) {
        User user = new User();
        BeanUtils.copyProperties(userDO, user);
        user.setId(String.valueOf(userDO.getId()));
        if (userDO.getEnabled().equals(UserDO.DISABLED)) {
            user.setLock(true);
        } else {
            user.setLock(false);
        }
        return user;
    }
}
