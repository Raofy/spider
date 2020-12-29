package com.jin10.spider.spiderserver.service;

import com.jin10.spider.spiderserver.dto.SysRoleDTO;
import com.jin10.spider.spiderserver.entity.SysRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 角色 服务类
 * </p>
 *
 * @author Airey
 * @since 2019-11-08
 */
public interface ISysRoleService extends IService<SysRole> {


    /**
     * 新增用户角色
     *
     * @param sysRoleDTO
     * @return
     */
    boolean insertSysRole(SysRoleDTO sysRoleDTO);


    /**
     * 根据用户角色id删除对应的用户角色
     *
     * @param roleId
     * @return
     */
    boolean deleteByRoleId(Long roleId);


    /**
     * 修改用户角色
     *
     * @param sysRoleDTO
     * @return
     */
    boolean updateSysRole(SysRoleDTO sysRoleDTO);


    /**
     * 查询系统角色菜单
     *
     * @return
     */
    List<SysRole> findSysRoleList();


}
