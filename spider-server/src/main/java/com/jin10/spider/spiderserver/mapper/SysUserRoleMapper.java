package com.jin10.spider.spiderserver.mapper;

import com.jin10.spider.spiderserver.entity.SysUserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 用户与角色对应关系 Mapper 接口
 * </p>
 *
 * @author Airey
 * @since 2019-11-08
 */
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {


    /**
     * 根据用户id查询用户角色关系
     *
     * @param userId
     * @return
     */
    List<SysUserRole> selectUserRoleListByUserId(Long userId);

}
