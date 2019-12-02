package com.lhc.ocat.mobileplatform.service;

import com.lhc.ocat.mobileplatform.domain.dto.Menu;
import com.lhc.ocat.mobileplatform.exception.ApiException;

import java.util.List;

/**
 * @author lhc
 * @date 2019-11-27 23:09
 */
public interface SystemMenuService {

    /**
     * 创建菜单
     * @param parentId 父菜单id
     * @param orderNum 排序号
     * @param type 菜单类型
     * @param name 菜单名称
     * @param icon 菜单图标
     * @param href 菜单跳转链接
     * @param description 菜单描述
     * @throws ApiException 业务异常
     */
    void createMenu(Long parentId,
                    Integer orderNum,
                    Integer type,
                    String name,
                    String icon,
                    String href,
                    String description) throws ApiException;

    /**
     * 修改菜单
     * @param menuId 菜单id
     * @param parentId 父菜单id
     * @param orderNum 排序号
     * @param type 菜单类型
     * @param name 菜单名称
     * @param icon 菜单图标
     * @param href 菜单跳转链接
     * @param description 菜单描述
     * @throws ApiException 业务异常
     */
    void updateMenu(Long menuId,
                    Long parentId,
                    Integer orderNum,
                    Integer type,
                    String name,
                    String icon,
                    String href,
                    String description) throws ApiException;

    /**
     * 删除菜单
     * @param menuId 菜单id
     * @throws ApiException 业务异常
     */
    void deleteMenu(Long menuId) throws ApiException;

    /**
     * 查询所有菜单
     * @return 菜单列表
     * @throws ApiException 业务异常
     */
    List<Menu> listMenu() throws ApiException;
}
