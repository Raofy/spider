package com.jin10.spider.spiderserver.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jin10.spider.common.exception.BaseException;
import com.jin10.spider.spiderserver.entity.PreUser;
import com.jin10.spider.spiderserver.entity.SysGroupMonitor;
import com.jin10.spider.spiderserver.entity.SysUserGroup;
import com.jin10.spider.spiderserver.form.SysGroupMonitorFrom;
import com.jin10.spider.spiderserver.mapper.SysGroupMonitorMapper;
import com.jin10.spider.spiderserver.service.ISysGroupMonitorService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jin10.spider.spiderserver.service.ISysUserGroupService;
import com.jin10.spider.spiderserver.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 分组-源  黑名单 服务实现类
 * </p>
 *
 * @author Airey
 * @since 2020-08-28
 */
@Service
@Slf4j
public class SysGroupMonitorServiceImpl extends ServiceImpl<SysGroupMonitorMapper, SysGroupMonitor> implements ISysGroupMonitorService {
    @Autowired
    private ISysUserGroupService sysUserGroupService;

    @Override
    public void setHidden(SysGroupMonitorFrom sysGroupMonitorFrom) {
        PreUser user = SecurityUtil.getUser();
        //获取分组
        SysUserGroup userGroup = sysUserGroupService.getOne(Wrappers.<SysUserGroup>lambdaQuery().eq(SysUserGroup::getUserId, user.getUserId()));
        if(userGroup == null){
            throw new BaseException("当前用户没有加入分组！！");
        }
        if(userGroup.getGroupId() == 1){
            throw new BaseException("默认分组不能屏蔽源！！");
        }
        SysGroupMonitor sysGroupMonitor = new SysGroupMonitor();
        BeanUtils.copyProperties(sysGroupMonitorFrom,sysGroupMonitor);
        sysGroupMonitor.setUpdator(user.getUsername());
        sysGroupMonitor.setGroupId(userGroup.getGroupId());
        sysGroupMonitor.setInsertTime(new Date());

        try {
            save(sysGroupMonitor);
        } catch (DuplicateKeyException e) {
            log.info("已经设置为隐藏过了");
        }
    }

    @Override
    public void setDisplay(SysGroupMonitorFrom sysGroupMonitorFrom) {
        PreUser user = SecurityUtil.getUser();
        //获取分组
        SysUserGroup userGroup = sysUserGroupService.getOne(Wrappers.<SysUserGroup>lambdaQuery().eq(SysUserGroup::getUserId, user.getUserId()));
        if(userGroup == null){
            throw new BaseException("当前用户没有加入分组！！");
        }
        remove(Wrappers.<SysGroupMonitor>lambdaQuery().eq(SysGroupMonitor::getGroupId,userGroup.getGroupId())
                .eq(SysGroupMonitor::getSource,sysGroupMonitorFrom.getSource())
                .eq(SysGroupMonitor::getCategory,sysGroupMonitorFrom.getCategory())
                .eq(SysGroupMonitor::getChannel,sysGroupMonitorFrom.getChannel()));
    }

    @Override
    public List<SysGroupMonitor> filterList() {
        PreUser user = SecurityUtil.getUser();
        //获取分组
        SysUserGroup userGroup = sysUserGroupService.getOne(Wrappers.<SysUserGroup>lambdaQuery().eq(SysUserGroup::getUserId, user.getUserId()));
        if(userGroup == null){
            return null;
        }
        List<SysGroupMonitor> list = list(Wrappers.<SysGroupMonitor>lambdaQuery().eq(SysGroupMonitor::getGroupId, userGroup.getGroupId()));
        return list;
    }

    @Override
    public void batchSetHidden(List<SysGroupMonitorFrom> fromList) {
        PreUser user = SecurityUtil.getUser();
        //获取分组
        SysUserGroup userGroup = sysUserGroupService.getOne(Wrappers.<SysUserGroup>lambdaQuery().eq(SysUserGroup::getUserId, user.getUserId()));
        if(userGroup == null){
            throw new BaseException("当前用户没有加入分组！！");
        }
        if(userGroup.getGroupId() == 1){
            throw new BaseException("默认分组不能屏蔽源！！");
        }

        SysGroupMonitor sysGroupMonitor;
        for(SysGroupMonitorFrom sysGroupMonitorFrom : fromList){
            sysGroupMonitor = new SysGroupMonitor();
            BeanUtils.copyProperties(sysGroupMonitorFrom,sysGroupMonitor);
            sysGroupMonitor.setUpdator(user.getUsername());
            sysGroupMonitor.setGroupId(userGroup.getGroupId());
            sysGroupMonitor.setInsertTime(new Date());
            try {
                save(sysGroupMonitor);
            } catch (DuplicateKeyException e) {
                log.info("已经设置为隐藏过了");
            }
        }

    }
}
