package com.jin10.spider.spiderserver.service.impl;

import com.jin10.spider.spiderserver.entity.SysMenu;
import com.jin10.spider.spiderserver.mapper.SysMenuMapper;
import com.jin10.spider.spiderserver.service.ISysMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 菜单管理 服务实现类
 * </p>
 *
 * @author Airey
 * @since 2019-11-08
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements ISysMenuService {

    /**
     * 根据用户id查询权限
     *
     * @param userId
     * @return
     */
    @Override
    public List<String> findPermsByUserId(Long userId) {
        return baseMapper.findPermsByUserId(userId);
    }

    /**
     * 查询系统菜单列表
     *
     * @return
     */
    @Override
    public List<SysMenu> findSysMenuList() {
        return baseMapper.selectList(null);
    }
}
