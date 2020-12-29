package com.jin10.spider.spiderserver.service.impl;

import com.jin10.spider.spiderserver.entity.SysUserRole;
import com.jin10.spider.spiderserver.mapper.SysUserRoleMapper;
import com.jin10.spider.spiderserver.service.ISysUserRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户与角色对应关系 服务实现类
 * </p>
 *
 * @author Airey
 * @since 2019-11-08
 */
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements ISysUserRoleService {

    /**
     * 根据用户id查询用户角色关系
     *
     * @param userId
     * @return
     */
    @Override
    public List<SysUserRole> selectUserRoleListByUserId(Long userId) {

        return baseMapper.selectUserRoleListByUserId(userId);
    }
}
