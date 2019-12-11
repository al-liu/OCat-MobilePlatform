package com.lhc.ocat.mobileplatform.util;

import com.lhc.ocat.mobileplatform.domain.dos.MenuDO;
import com.lhc.ocat.mobileplatform.domain.dto.Menu;
import lombok.experimental.UtilityClass;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lhc
 * @date 2019-12-09 08:56
 */
@UtilityClass
public class MenuUtil {

    /**
     * 将菜单列表按照父子关系整理成嵌套的菜单列表
     * @param menuList 所有菜单列表
     * @return 整理后的菜单列表
     */
    public static List<Menu> filterMenusFromRootNode(List<Menu> menuList) {
        return filterMenus(Menu.ROOT_PARENT_ID, menuList);
    }

    private static List<Menu> filterMenus(String parentId, List<Menu> menuList) {
        List<Menu> list =  menuList.stream()
                .filter(menu -> menu.getParentId().equals(parentId))
                .collect(Collectors.toList());
        Collections.sort(list);
        for (Menu menu :
                list) {
            List<Menu> children = filterMenus(menu.getId(), menuList);
            menu.setChildren(children);
        }
        return list;
    }

    public static List<Menu> filterButtons(List<Menu> menuList) {
        return menuList.stream().filter(menu -> menu.getType().equals(MenuDO.BUTTON_TYPE)).collect(Collectors.toList());
    }
}
