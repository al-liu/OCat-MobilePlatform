package com.lhc.ocat.mobileplatform.controller;

import com.lhc.ocat.mobileplatform.domain.dto.Menu;
import com.lhc.ocat.mobileplatform.domain.dto.Permission;
import com.lhc.ocat.mobileplatform.domain.dto.Role;
import com.lhc.ocat.mobileplatform.domain.dto.User;
import com.lhc.ocat.mobileplatform.domain.param.UserLockParam;
import com.lhc.ocat.mobileplatform.domain.param.system.MenuParam;
import com.lhc.ocat.mobileplatform.domain.param.system.PermissionParam;
import com.lhc.ocat.mobileplatform.domain.param.system.RoleParam;
import com.lhc.ocat.mobileplatform.domain.param.system.UserParam;
import com.lhc.ocat.mobileplatform.domain.vo.Result;
import com.lhc.ocat.mobileplatform.service.SystemMenuService;
import com.lhc.ocat.mobileplatform.service.SystemPermissionService;
import com.lhc.ocat.mobileplatform.service.SystemRoleService;
import com.lhc.ocat.mobileplatform.service.SystemUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

/**
 * @author lhc
 * @date 2019-11-27 14:31
 */
@RestController
@Validated
@RequestMapping(value = "/system")
public class SystemManageController {

    @Autowired
    private SystemUserService userService;
    @Autowired
    private SystemRoleService roleService;
    @Autowired
    private SystemPermissionService permissionService;
    @Autowired
    private SystemMenuService menuService;

    @PostMapping(value = "/user")
    public Result createUser(@RequestBody @Validated UserParam userParam) {
        userService.createUser(userParam.getUsername(), userParam.getPassword(), userParam.getDescription());
        return Result.success();
    }

    @PutMapping(value = "/user/{id:[0-9]+}")
    public Result updateUser(@PathVariable Long id, @RequestBody @Validated UserParam userParam) {
        userService.updateUser(id,
                userParam.getUsername(),
                userParam.getPassword(),
                userParam.getDescription());
        return Result.success();
    }

    @DeleteMapping(value = "/user/{id:[0-9]+}")
    public Result deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }

    @GetMapping(value = "/user/all")
    public Result listUser() {
        List<User> list = userService.listUser();
        return Result.success(list);
    }

    @GetMapping(value = "/user/{id:[0-9]+}/roles")
    public Result listUserRoles(@PathVariable Long id) {
        List<Role> list = userService.listRoleByUserId(id);
        return Result.success(list);
    }

    @PutMapping(value = "/user/{userId:[0-9]+}/role/{roleId:[0-9]+}")
    public Result allotRole(@PathVariable Long userId, @PathVariable Long roleId) {
        userService.allotRole(userId, roleId);
        return Result.success();
    }

    @PutMapping(value = "/user/{userId:[0-9]+}/roles")
    public Result allotRoles(@PathVariable Long userId, @RequestBody @NotNull Set<String> roleList) {
        userService.allotRoles(userId, roleList);
        return Result.success();
    }

    @PutMapping(value = "/user/{userId:[0-9]+}/enable")
    public Result enableUser(@PathVariable Long userId, @RequestBody UserLockParam userLockParam) {
        userService.enableUser(userId, userLockParam.getEnabled());
        return Result.success();
    }

    @PostMapping(value = "/role")
    public Result createRole(@RequestBody @Validated RoleParam roleParam) {
        roleService.createRole(roleParam.getCode(), roleParam.getName(), roleParam.getDescription());
        return Result.success();
    }

    @PutMapping(value = "/role/{id:[0-9]+}")
    public Result updateRole(@PathVariable Long id, @RequestBody @Validated RoleParam roleParam) {
        roleService.updateRole(id, roleParam.getCode(), roleParam.getName(), roleParam.getDescription());
        return Result.success();
    }

    @DeleteMapping(value = "/role/{id:[0-9]+}")
    public Result deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return Result.success();
    }

    @GetMapping(value = "/role/all")
    public Result listRole() {
        List<Role> list = roleService.listRole();
        return Result.success(list);
    }

    @PutMapping(value = "/role/{roleId:[0-9]+}/permissions")
    public Result allotPermissions(@PathVariable Long roleId, @RequestBody @NotNull Set<String> permissionList) {
        roleService.allotPermissions(roleId, permissionList);
        return Result.success();
    }

    @PutMapping(value = "/role/{roleId:[0-9]+}/menus")
    public Result allotMenus(@PathVariable Long roleId, @RequestBody @NotNull Set<String> menuList) {
        roleService.allotMenus(roleId, menuList);
        return Result.success();
    }

    @GetMapping(value = "/role/{id:[0-9]+}/permissions")
    public Result listRolePermissions(@PathVariable Long id) {
        List<Permission> list = roleService.listPermissionByRoleId(id);
        return Result.success(list);
    }

    @GetMapping(value = "/role/{id:[0-9]+}/menus")
    public Result listRoleMenus(@PathVariable Long id) {
        List<Menu> list = roleService.listMenuByRoleId(id);
        return Result.success(list);
    }

    @PostMapping(value = "/permission")
    public Result createPermission(@RequestBody @Validated PermissionParam permissionParam) {
        permissionService.createPermission(permissionParam.getCode(), permissionParam.getName(), permissionParam.getDescription());
        return Result.success();
    }

    @PutMapping(value = "/permission/{id:[0-9]+}")
    public Result updatePermission(@PathVariable Long id, @RequestBody @Validated PermissionParam permissionParam) {
        permissionService.updatePermission(id, permissionParam.getCode(), permissionParam.getName(), permissionParam.getDescription());
        return Result.success();
    }

    @DeleteMapping(value = "/permission/{id:[0-9]+}")
    public Result deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
        return Result.success();
    }

    @GetMapping(value = "/permission/all")
    public Result listPermission() {
        List<Permission> list = permissionService.listPermission();
        return Result.success(list);
    }

    @PostMapping(value = "/menu")
    public Result createMenu(@RequestBody @Validated MenuParam menuParam) {
        menuService.createMenu(menuParam.getParentId(),
                menuParam.getOrderNum(),
                menuParam.getType(),
                menuParam.getName(),
                menuParam.getIcon(),
                menuParam.getHref(),
                menuParam.getDescription());
        return Result.success();
    }

    @PutMapping(value = "/menu/{id:[0-9]+}")
    public Result updateMenu(@PathVariable Long id, @RequestBody @Validated MenuParam menuParam) {
        menuService.updateMenu(id,
                menuParam.getParentId(),
                menuParam.getOrderNum(),
                menuParam.getType(),
                menuParam.getName(),
                menuParam.getIcon(),
                menuParam.getHref(),
                menuParam.getDescription());
        return Result.success();
    }

    @DeleteMapping(value = "/menu/{id:[0-9]+}")
    public Result deleteMenu(@PathVariable Long id) {
        menuService.deleteMenu(id);
        return Result.success();
    }

    @GetMapping(value = "/menu/all")
    public Result listMenu() {
        List<Menu> list = menuService.listMenu();
        return Result.success(list);
    }
}
