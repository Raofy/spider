package com.jin10.spider.spiderserver.mapper;

import com.jin10.spider.spiderserver.entity.SysMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 菜单管理 Mapper 接口
 * </p>
 *
 * @author Airey
 * @since 2019-11-08
 */
public interface SysMenuMapper extends BaseMapper<SysMenu> {


    /**
     * 根据用户id查询用户权限
     *
     * @param userId
     * @return
     */
    List<String> findPermsByUserId(Long userId);


    /**
     * 根据用户角色id查询对应的角色权限列表
     *
     * @param roleId
     * @return
     */
    List<SysMenu> findSysMenuByRoleId(Long roleId);


    /**
     * 根据用户id查询对应的菜单列表
     *
     * @param userId
     * @return
     */
    List<SysMenu> findSysMenuByUserId(Long userId);

}
