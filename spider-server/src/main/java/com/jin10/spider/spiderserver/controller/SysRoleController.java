package com.jin10.spider.spiderserver.controller;


import com.jin10.spider.common.bean.BaseResponse;
import com.jin10.spider.spiderserver.dto.SysRoleDTO;
import com.jin10.spider.spiderserver.entity.SysRole;
import com.jin10.spider.spiderserver.service.ISysRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 角色 前端控制器
 * </p>
 *
 * @author Airey
 * @since 2019-11-08
 */
@RestController
@RequestMapping("/sys-role")
@Slf4j
//@PreAuthorize("hasAuthority('sys:role')")
public class SysRoleController {

    @Autowired
    private ISysRoleService roleService;


    /**
     * 新增用户角色
     *
     * @param sysRoleDTO
     * @return
     */
    @PostMapping("add")
    public BaseResponse insertSysRole(@RequestBody SysRoleDTO sysRoleDTO) {
        log.info("新增用户的角色信息为 ==>{}", sysRoleDTO);
        boolean b = roleService.insertSysRole(sysRoleDTO);
        return BaseResponse.ok(b);
    }

    /**
     * 根据用户角色id删除用户角色
     *
     * @param roleId
     * @return
     */
    @GetMapping("delete/{roleId}")
    public BaseResponse deleteByRoleId(@PathVariable("roleId") Long roleId) {
        log.info("删除角色id为 { " + roleId + " } 的角色");
        boolean b = roleService.deleteByRoleId(roleId);
        return BaseResponse.ok(b);
    }

    /**
     * 更新用户角色信息
     *
     * @param sysRoleDTO
     * @return
     */
    @PostMapping("update")
    public BaseResponse updateSysRole(@RequestBody SysRoleDTO sysRoleDTO) {
        log.info("更新系统用户的角色信息为 ==> {}", sysRoleDTO);
        boolean b = roleService.updateSysRole(sysRoleDTO);
        return BaseResponse.ok(b);
    }

    /**
     * 查询系统用户角色列表(包含菜单)
     *
     * @return
     */
    @GetMapping("listincludeMenu")
    public BaseResponse findSysRoleListinCludeMenu() {
        log.info("查询系统的角色列表");
        List<SysRole> sysRoleList = roleService.findSysRoleList();
        return BaseResponse.ok(sysRoleList);
    }


    /**
     * 查询系统用户角色列表
     *
     * @return
     */
    @GetMapping("list")
    public BaseResponse findSysRoleList() {
        log.info("查询系统的角色列表");
        List<SysRole> list = roleService.list();
        return BaseResponse.ok(list);
    }

}
