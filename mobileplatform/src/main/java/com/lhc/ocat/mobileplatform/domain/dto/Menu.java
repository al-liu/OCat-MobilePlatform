package com.lhc.ocat.mobileplatform.domain.dto;

import com.lhc.ocat.mobileplatform.domain.dos.ApplicationDO;
import com.lhc.ocat.mobileplatform.domain.dos.MenuDO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author lhc
 * @date 2019-11-23 23:34
 */
@Data
@EqualsAndHashCode
public class Menu implements Comparable<Menu>{
    private String id;
    private String parentId;
    private Integer orderNum;
    private Integer type;
    private String name;
    private String icon;
    private String href;
    private String description;

    private List<Menu> children = new ArrayList<>();
    public static final String ROOT_PARENT_ID = "0";

    static public Menu toMenu(MenuDO menuDO) {
        Menu menu = new Menu();
        BeanUtils.copyProperties(menuDO, menu);
        menu.setId(String.valueOf(menuDO.getId()));
        menu.setParentId(String.valueOf(menuDO.getParentId()));
        return menu;
    }

    @Override
    public int compareTo(Menu o) {
        if (Objects.isNull(o)) {
            return -1;
        }
        if (Objects.isNull(this.getOrderNum())) {
            return 1;
        }
        return this.getOrderNum().compareTo(o.getOrderNum());
    }
}
