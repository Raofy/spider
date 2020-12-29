package com.jin10.spider.spiderserver.service;

import com.jin10.spider.spiderserver.entity.SysMenu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 菜单管理 服务类
 * </p>
 *
 * @author Airey
 * @since 2019-11-08
 */
public interface ISysMenuService extends IService<SysMenu> {

    /**
     * 根据用户id查询权限
     *
     * @param userId
     * @return
     */
    List<String> findPermsByUserId(Long userId);


    /**
     * 查询系统菜单列表
     *
     * @return
     */
    List<SysMenu> findSysMenuList();


}
