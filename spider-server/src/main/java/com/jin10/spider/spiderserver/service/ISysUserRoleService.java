package com.jin10.spider.spiderserver.service;

import com.jin10.spider.spiderserver.entity.SysUserRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 用户与角色对应关系 服务类
 * </p>
 *
 * @author Airey
 * @since 2019-11-08
 */
public interface ISysUserRoleService extends IService<SysUserRole> {

    /**
     * 根据用户id查询用户角色关系
     * @param userId
     * @return
     */
    List<SysUserRole> selectUserRoleListByUserId(Long userId);


}
