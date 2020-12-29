package com.jin10.spider.spiderserver.service;

import com.jin10.spider.spiderserver.entity.SysUser;
import com.jin10.spider.spiderserver.entity.SysUserGroup;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Collection;


/**
 * <p>
 * 用户分组 服务类
 * </p>
 *
 * @author Airey
 * @since 2020-08-27
 */
public interface ISysUserGroupService extends IService<SysUserGroup> {

    Collection<SysUser> listByGroup(Long groupId);

    /**
     * 给分组添加用户
     * @param groupId
     * @param userId
     */
    void addUser(int groupId,long userId);

    /**
     * 分组移出用户
     * @param groupId
     * @param userId
     */
    void removeUser(int groupId,long userId);

    /**
     * 添加到默认分组
     * @param userId
     */
    void saveDefault(long userId);

}

