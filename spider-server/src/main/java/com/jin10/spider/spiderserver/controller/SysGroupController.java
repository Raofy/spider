package com.jin10.spider.spiderserver.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jin10.spider.common.bean.BasePageRequest;
import com.jin10.spider.common.bean.BaseResponse;
import com.jin10.spider.common.exception.BaseException;
import com.jin10.spider.common.utils.QueryPage;
import com.jin10.spider.spiderserver.constants.GlobalConstants;
import com.jin10.spider.spiderserver.dto.SysUserDTO;
import com.jin10.spider.spiderserver.entity.SysGroup;
import com.jin10.spider.spiderserver.entity.SysUser;
import com.jin10.spider.spiderserver.entity.SysUserGroup;
import com.jin10.spider.spiderserver.service.ISysGroupService;
import com.jin10.spider.spiderserver.service.ISysUserGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 分组 前端控制器
 * </p>
 *
 * @author Airey
 * @since 2020-08-27
 */
@RestController
@RequestMapping("sys-group")
public class SysGroupController {

    @Autowired
    private ISysGroupService sysGroupService;
    @Autowired
    ISysUserGroupService sysUserGroupService;



    @GetMapping("page")
    public BaseResponse page(BasePageRequest pageRequest, String name){

        IPage<SysGroup> page = sysGroupService.page(new QueryPage<SysGroup>().getPage(pageRequest), Wrappers.<SysGroup>lambdaQuery().like(StringUtils.isNotEmpty(name), SysGroup::getName, name));
        return BaseResponse.ok(page);
    }

    @PostMapping("save")
    public BaseResponse save(String name){
        Assert.notNull(name,"分组名称不能为空");
        SysGroup sysGroup = new SysGroup();
        sysGroup.setName(name);
        sysGroup.setInsertTime(new Date());
        try {
            sysGroupService.save(sysGroup);
        } catch (DuplicateKeyException e) {
            return BaseResponse.error("分组名已存在");
        }
        return BaseResponse.ok();
    }


    @PostMapping("update")
    public BaseResponse update(@RequestBody SysGroup sysGroup){
        Assert.notNull(sysGroup,"参数不能为空");
        Assert.notNull(sysGroup.getId(),"参数不能为空");
        Assert.notNull(sysGroup.getName(),"名称不能为空");
        sysGroup.setUpdateTime(new Date());
        SysGroup group = sysGroupService.getById(sysGroup.getId());
        if(group.getName().equals(GlobalConstants.GROUP_NAME)){
            throw new BaseException("默认分组不能删除");
        }
        try {
            sysGroupService.updateById(sysGroup);
        } catch (DuplicateKeyException e) {
            return BaseResponse.error("分组名已存在");
        }
        return BaseResponse.ok();
    }

    @PostMapping("delete")
    public BaseResponse delete(Integer id){
        Assert.notNull(id,"参数错误");
        SysGroup group = sysGroupService.getById(id);
        if(group.getName().equals(GlobalConstants.GROUP_NAME)){
            throw new BaseException("默认分组不能删除");
        }
        sysGroupService.removeById(id);
        return BaseResponse.ok();
    }

    /**
     * 获取分组用户人数
     * @param groupId
     * @return
     */
    @GetMapping("listByGroup")
    public BaseResponse listByGroup(Long groupId){
        Assert.notNull(groupId,"参数错误");
        Collection<SysUser> sysUserDTOS = sysUserGroupService.listByGroup(groupId);
        return BaseResponse.ok(sysUserDTOS);
    }

    /**
     * 分组添加用户
     * @param groupId
     * @param userId
     * @return
     */
    @PostMapping("addUser")
    public BaseResponse addUser(int groupId,long userId){
        Assert.notNull(userId,"参数错误");
        Assert.notNull(groupId,"参数错误");
        sysUserGroupService.addUser(groupId,userId);
        return BaseResponse.ok();
    }

    /**
     * 分组删除用户
     * @param groupId
     * @param userId
     * @return
     */
    @PostMapping("removeUser")
    public BaseResponse removeUser(int groupId,long userId){
        Assert.notNull(userId,"参数错误");
        Assert.notNull(groupId,"参数错误");
        sysUserGroupService.removeUser(groupId,userId);
        return BaseResponse.ok();
    }


}
