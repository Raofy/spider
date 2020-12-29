package com.jin10.spider.spiderserver.controller;


import com.jin10.spider.common.bean.BaseResponse;
import com.jin10.spider.spiderserver.entity.SysGroupMonitor;
import com.jin10.spider.spiderserver.service.ISysGroupMonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 分组-源  黑名单 前端控制器
 * </p>
 *
 * @author Airey
 * @since 2020-08-28
 */
@RestController
@RequestMapping("/sys-group-monitor")
public class SysGroupMonitorController {

    @Autowired
    private ISysGroupMonitorService sysGroupMonitorService;

    @GetMapping("filterList")
    public BaseResponse filterList(){
        List<SysGroupMonitor> sysGroupMonitorList = sysGroupMonitorService.filterList();
        return BaseResponse.ok(sysGroupMonitorList);
    }

}
