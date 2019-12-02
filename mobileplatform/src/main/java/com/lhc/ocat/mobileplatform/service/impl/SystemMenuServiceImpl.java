package com.lhc.ocat.mobileplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lhc.ocat.mobileplatform.domain.dos.MenuDO;
import com.lhc.ocat.mobileplatform.domain.dto.Menu;
import com.lhc.ocat.mobileplatform.exception.ApiErrorType;
import com.lhc.ocat.mobileplatform.exception.ApiException;
import com.lhc.ocat.mobileplatform.mapper.MenuMapper;
import com.lhc.ocat.mobileplatform.service.SystemMenuService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author lhc
 * @date 2019-11-27 23:12
 */
@Service
public class SystemMenuServiceImpl implements SystemMenuService {

    @Autowired
    private MenuMapper menuMapper;

    @Override
    public void createMenu(Long parentId, Integer orderNum, Integer type, String name, String icon, String href, String description) throws ApiException {
        MenuDO menuDO = new MenuDO();
        menuDO.setParentId(parentId);
        menuDO.setOrderNum(orderNum);
        menuDO.setType(type);
        menuDO.setName(name);
        menuDO.setIcon(icon);
        menuDO.setHref(href);
        menuDO.setDescription(description);
        menuMapper.insert(menuDO);
    }

    @Override
    public void updateMenu(Long menuId, Long parentId, Integer orderNum, Integer type, String name, String icon, String href, String description) throws ApiException {
        MenuDO menuDO = menuMapper.selectById(menuId);
        if (Objects.isNull(menuDO)) {
            throw new ApiException(ApiErrorType.SYSTEM_MENU_NOT_FOUND_ERROR);
        }
        menuDO.setParentId(parentId);
        menuDO.setOrderNum(orderNum);
        menuDO.setType(type);
        menuDO.setName(name);
        menuDO.setIcon(icon);
        menuDO.setHref(href);
        menuDO.setDescription(description);
        menuMapper.updateById(menuDO);
    }

    @Override
    public void deleteMenu(Long menuId) throws ApiException {
        MenuDO menuDO = menuMapper.selectById(menuId);
        if (Objects.isNull(menuDO)) {
            throw new ApiException(ApiErrorType.SYSTEM_MENU_NOT_FOUND_ERROR);
        }
        menuMapper.deleteById(menuId);
    }

    @Override
    public List<Menu> listMenu() throws ApiException {
        List<MenuDO> menuDOList = menuMapper.selectList(
                new LambdaQueryWrapper<MenuDO>()
                        .orderByDesc(MenuDO::getCreateTime));
        List<Menu> menuList = new ArrayList<>();
        for (MenuDO menuDO :
                menuDOList) {
            Menu menu = new Menu();
            BeanUtils.copyProperties(menuDO, menu);
            menuList.add(menu);
        }
        return menuList;
    }
}
