package com.jin10.spider.spiderserver.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jin10.spider.common.exception.BaseException;
import com.jin10.spider.spiderserver.constants.GlobalConstants;
import com.jin10.spider.spiderserver.dto.SysUserDTO;
import com.jin10.spider.spiderserver.entity.SysGroup;
import com.jin10.spider.spiderserver.entity.SysUser;
import com.jin10.spider.spiderserver.entity.SysUserGroup;
import com.jin10.spider.spiderserver.mapper.SysUserGroupMapper;
import com.jin10.spider.spiderserver.service.ISysGroupService;
import com.jin10.spider.spiderserver.service.ISysUserGroupService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jin10.spider.spiderserver.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户分组 服务实现类
 * </p>
 *
 * @author Airey
 * @since 2020-08-27
 */
@Service
public class SysUserGroupServiceImpl extends ServiceImpl<SysUserGroupMapper, SysUserGroup> implements ISysUserGroupService {

    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private ISysGroupService sysGroupService;

    @Override
    public Collection<SysUser> listByGroup(Long groupId) {
        List<SysUserGroup> list = list(Wrappers.<SysUserGroup>lambdaQuery().eq(SysUserGroup::getGroupId, groupId));
        if(list == null || list.isEmpty()){
            return null;
        }
        List<Long> ids = list.stream().map(SysUserGroup::getUserId).collect(Collectors.toList());
        Collection<SysUser> sysUsers = sysUserService.listByIds(ids);
        return sysUsers;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addUser(int groupId, long userId) {
        SysUserGroup sysUserGroup = new SysUserGroup();
        sysUserGroup.setUserId(userId);
        sysUserGroup.setGroupId(groupId);
        removeById(userId);
        save(sysUserGroup);
    }

    @Override
    public void removeUser(int groupId, long userId) {
        SysGroup group = sysGroupService.getById(groupId);
        if(group.getName().equals(GlobalConstants.GROUP_NAME)){
            throw new BaseException("不能从默认分组删除用户");
        }
        removeById(userId);
        saveDefault(userId);
    }

    @Override
    public void saveDefault(long userId) {
        SysGroup group = sysGroupService.getOne(Wrappers.<SysGroup>lambdaQuery().eq(SysGroup::getName, GlobalConstants.GROUP_NAME));
        if(group != null){
            addUser(group.getId(),userId);
        }
    }
}
