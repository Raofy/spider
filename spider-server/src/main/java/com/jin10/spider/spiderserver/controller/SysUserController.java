package com.jin10.spider.spiderserver.controller;


import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jin10.spider.common.bean.BaseResponse;
import com.jin10.spider.common.exception.BaseException;
import com.jin10.spider.common.utils.PageUtils;
import com.jin10.spider.spiderserver.dto.SysUserDTO;
import com.jin10.spider.spiderserver.entity.SysRole;
import com.jin10.spider.spiderserver.entity.SysUser;
import com.jin10.spider.spiderserver.entity.SysUserRole;
import com.jin10.spider.spiderserver.form.SysUserForm;
import com.jin10.spider.spiderserver.service.ISysUserRoleService;
import com.jin10.spider.spiderserver.service.ISysUserService;
import com.jin10.spider.spiderserver.utils.PreUtil;
import com.jin10.spider.spiderserver.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * 系统用户表 前端控制器
 * </p>
 *
 * @author Airey
 * @since 2019-11-08
 */
@RestController
@RequestMapping("/sys-user")
@Slf4j
//@PreAuthorize("hasAuthority('sys:user')")
public class SysUserController {

    @Autowired
    private ISysUserService userService;
    @Autowired
    private ISysUserRoleService userRoleService;


    /**
     * 保存用户信息，包含角色
     *
     * @param sysUserDTO
     * @return
     */
    @PostMapping("add")
    public BaseResponse insert(@RequestBody SysUserDTO sysUserDTO) {
        log.info("新增系统用户信息为 ==> " + sysUserDTO);
        String phone = sysUserDTO.getPhone();
        boolean checkPhonemoj = userService.checkPhonemoj(phone);
        if (!checkPhonemoj) {
            throw new BaseException("手机号码不合法,请检查是否是钉钉账号！");
        }
        boolean b = userService.insertUser(sysUserDTO);
        return BaseResponse.ok(b);
    }

    /**
     * 校验电话号码是否在职
     *
     * @param phone
     * @return
     */
    @GetMapping("checkPhone/{phone}")
    public BaseResponse checkPhone(@PathVariable("phone") String phone) {
        boolean b = userService.checkPhonemoj(phone);
        return BaseResponse.ok(b);
    }

    /**
     * 根据主键id删除用户
     *
     * @param userId
     * @return
     */
    @GetMapping("delete/{userId}")
    public BaseResponse delete(@PathVariable("userId") Long userId) {
        log.info("删除系统用户,主键ID为 ==> {}", userId);
        boolean b = userService.deleteUser(userId);
        return BaseResponse.ok(b);
    }

    /**
     * 更新用户信息
     *
     * @param sysUserForm
     * @return
     */
    @PostMapping("update")
    public BaseResponse update(@RequestBody SysUserForm sysUserForm) {
        log.info("更新系统用户的信息为 ==> {}", sysUserForm);
        String phone = sysUserForm.getPhone();
        boolean checkPhonemoj = userService.checkPhonemoj(phone);
        if (!checkPhonemoj) {
            throw new BaseException("手机号码不合法,请检查是否是钉钉账号！");
        }
        boolean b = userService.updateUser(sysUserForm);
        return BaseResponse.ok(b);
    }

    /**
     * 更新用户维护信息
     *
     * @param sysUserForm
     * @return
     */
    @PostMapping("updateMaintainer")
    public BaseResponse updateMaintainer(@RequestBody SysUserForm sysUserForm) {
        log.info("更新系统用户维护信息");
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(sysUserForm, sysUser);
        sysUser.setUpdateTime(new Date());
        boolean b = userService.updateById(sysUser);
        return BaseResponse.ok(b);
    }


    /**
     * 查询用户列表
     *
     * @param userForm
     * @return
     */
    @PostMapping("list")
    public BaseResponse list(@RequestBody SysUserForm userForm) {
        log.info("查询系统用户列表 ");
        IPage<SysUser> sysUserList = userService.findSysUserList(userForm);
        return BaseResponse.ok(new PageUtils(sysUserList));
    }

    /**
     * 重置用户密码
     */
    @GetMapping("/reset/{userId}")
    public BaseResponse reset(@PathVariable("userId") Long userId) {
        log.info("重置用户id为 " + userId + " 的密码！");
        SysUser sysUser = userService.getById(userId);
        SysUser updateUser = new SysUser();
        updateUser.setUserId(userId);
        if (StringUtils.isNotBlank(sysUser.getPhone())) {
            updateUser.setPassword(PreUtil.encode(sysUser.getPhone()));
        } else {
            updateUser.setPassword(PreUtil.encode("111111"));
        }
        boolean b = userService.updateById(updateUser);
        return BaseResponse.ok(b);
    }


    /**
     * 根据用户主键查询系统用户
     *
     * @param userId
     * @return
     */
    @GetMapping("findById/{userId}")
    public BaseResponse findById(@PathVariable("userId") Long userId) {
        log.info("根据主键查询用户信息, userId={}", userId);
        SysUser byId = userService.getById(userId);
        if (ObjectUtil.isNull(byId)) {
            return BaseResponse.ok(byId);
        }

        List<SysUserRole> sysUserRoles = userRoleService.selectUserRoleListByUserId(userId);
        List<SysRole> sysRoleList = new ArrayList<>();
        sysUserRoles.forEach(item -> {
            SysRole sysRole = new SysRole();
            BeanUtils.copyProperties(item, sysRole);
            sysRoleList.add(sysRole);
        });

        byId.setRoleList(sysRoleList);
        byId.setPassword(null);

        return BaseResponse.ok(byId);
    }

}
