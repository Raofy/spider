package com.jin10.spider.spiderserver.service;

import com.jin10.spider.spiderserver.entity.SysGroupMonitor;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jin10.spider.spiderserver.form.SysGroupMonitorFrom;

import java.util.List;

/**
 * <p>
 * 分组-源  黑名单 服务类
 * </p>
 *
 * @author Airey
 * @since 2020-08-28
 */
public interface ISysGroupMonitorService extends IService<SysGroupMonitor> {

    /**
     * 设置隐藏
     * @param sysGroupMonitorFrom
     */
    void setHidden(SysGroupMonitorFrom sysGroupMonitorFrom);

    /**
     * 设置为显示
     * @param sysGroupMonitorFrom
     */
    void setDisplay(SysGroupMonitorFrom sysGroupMonitorFrom);

    /**
     * 获取过滤的列表
     * @return
     */
    List<SysGroupMonitor> filterList();

    /**
     * 批量设置隐藏
     * @param fromList
     */
    void batchSetHidden(List<SysGroupMonitorFrom> fromList);
}
