package com.jin10.spider.spiderserver.controller;


import com.jin10.spider.common.bean.BaseResponse;
import com.jin10.spider.spiderserver.entity.SysMenu;
import com.jin10.spider.spiderserver.service.ISysMenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 菜单管理 前端控制器
 * </p>
 *
 * @author Airey
 * @since 2019-11-08
 */
@RestController
@RequestMapping("/sys-menu")
@Slf4j
//@PreAuthorize("hasAuthority('sys:menu')")
public class SysMenuController {


    @Autowired
    private ISysMenuService menuService;


    @GetMapping("/list")
    public BaseResponse getSysMenuList() {
        log.info("查询系统菜单列表");
        List<SysMenu> sysMenuList = menuService.findSysMenuList();
        return BaseResponse.ok(sysMenuList);
    }

    @PostMapping("/add")
    public BaseResponse add(@RequestBody SysMenu sysMenu) {
        log.info("新增菜单的参数为: " + sysMenu);
        boolean save = menuService.save(sysMenu);
        return BaseResponse.ok(save);
    }

    @PostMapping("/update")
    public BaseResponse update(@RequestBody SysMenu sysMenu) {
        log.info("更新菜单的参数为: " + sysMenu);
        boolean b = menuService.updateById(sysMenu);
        return BaseResponse.ok(b);
    }

    @GetMapping("/delete/{menuId}")
    public BaseResponse delete(@PathVariable("menuId") Long menuId) {
        log.info("删除主键ID为 " + menuId+" 菜单记录!");
        boolean b = menuService.removeById(menuId);
        return BaseResponse.ok(b);
    }


}
