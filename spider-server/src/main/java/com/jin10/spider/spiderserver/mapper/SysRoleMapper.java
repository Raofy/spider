package com.jin10.spider.spiderserver.mapper;

import com.jin10.spider.spiderserver.entity.SysRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 角色 Mapper 接口
 * </p>
 *
 * @author Airey
 * @since 2019-11-08
 */
public interface SysRoleMapper extends BaseMapper<SysRole> {


    /**
     * 查询系统用角色列表
     * @return
     */
    List<SysRole> findSysRoleList();

}
