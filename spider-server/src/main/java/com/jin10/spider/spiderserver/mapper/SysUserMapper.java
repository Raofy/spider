package com.jin10.spider.spiderserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jin10.spider.spiderserver.entity.SysRole;
import com.jin10.spider.spiderserver.entity.SysUser;
import com.jin10.spider.spiderserver.dto.SysUserInfoDTO;

import java.util.List;

/**
 * <p>
 * 系统用户表 Mapper 接口
 * </p>
 *
 * @author Airey
 * @since 2019-11-08
 */
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 查询系统用户列表
     *
     * @return
     */
    IPage<SysUser> findSysUserList(Page page);

    /**
     * 根据userId查询对应的用户角色
     *
     * @param userId
     * @return
     */
    SysRole findSysRoleByUserId(Long userId);

    /**
     * 查询用户具体信息
     * @param userId
     * @return
     */
    SysUserInfoDTO getUserInfo(Long userId);

    /**
     * 查询维护人员列表
     * @return
     */
    List<SysUser> findMaintainer();

}
