package com.jin10.spider.spiderserver.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jin10.spider.spiderserver.dto.SysRoleDTO;
import com.jin10.spider.spiderserver.entity.SysRole;
import com.jin10.spider.spiderserver.entity.SysRoleMenu;
import com.jin10.spider.spiderserver.mapper.SysRoleMapper;
import com.jin10.spider.spiderserver.service.ISysRoleMenuService;
import com.jin10.spider.spiderserver.service.ISysRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 角色 服务实现类
 * </p>
 *
 * @author Airey
 * @since 2019-11-08
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {


    @Autowired
    private ISysRoleMenuService roleMenuService;


    /**
     * 新增用户角色
     *
     * @param sysRoleDTO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean insertSysRole(SysRoleDTO sysRoleDTO) {

        SysRole sysRole = new SysRole();
        BeanUtils.copyProperties(sysRoleDTO, sysRole);
        sysRole.setCreateTime(new Date());
        baseMapper.insert(sysRole);

        Long roleId = sysRole.getRoleId();
        List<Long> sysRoleMenuList = sysRoleDTO.getSysRoleMenuList();
        List<SysRoleMenu> sysRoleMenus = sysRoleMenuList.stream().map(item -> {
            SysRoleMenu sysRoleMenu = new SysRoleMenu();
            sysRoleMenu.setMenuId(item);
            sysRoleMenu.setRoleId(roleId);
            return sysRoleMenu;
        }).collect(Collectors.toList());
        boolean b = roleMenuService.saveBatch(sysRoleMenus);


        return b ;
    }

    /**
     * 根据用户角色id删除对应的用户角色
     *
     * @param roleId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteByRoleId(Long roleId) {

        baseMapper.deleteById(roleId);
        boolean b = roleMenuService.remove(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
        return b;
    }

    /**
     * 修改用户角色
     *
     * @param sysRoleDTO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateSysRole(SysRoleDTO sysRoleDTO) {

        boolean flag = true;

        SysRole sysRole = new SysRole();
        BeanUtils.copyProperties(sysRoleDTO, sysRole);
        baseMapper.updateById(sysRole);
        Long roleId = sysRole.getRoleId();


        if (CollUtil.isNotEmpty(sysRoleDTO.getSysRoleMenuList())) {
            roleMenuService.remove(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
            List<Long> sysRoleMenuList = sysRoleDTO.getSysRoleMenuList();
            List<SysRoleMenu> sysRoleMenus = sysRoleMenuList.stream().map(item -> {
                SysRoleMenu sysRoleMenu = new SysRoleMenu();
                sysRoleMenu.setMenuId(item);
                sysRoleMenu.setRoleId(roleId);
                return sysRoleMenu;
            }).collect(Collectors.toList());
            flag = roleMenuService.saveBatch(sysRoleMenus);
        }

        return flag;
    }

    /**
     * 查询系统角色菜单
     *
     * @return
     */
    @Override
    public List<SysRole> findSysRoleList() {
        List<SysRole> sysRoleList = baseMapper.findSysRoleList();
        return sysRoleList;
    }


}
