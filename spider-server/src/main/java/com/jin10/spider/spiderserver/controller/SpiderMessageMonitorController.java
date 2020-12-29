package com.jin10.spider.spiderserver.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jin10.spider.common.bean.BasePageRequest;
import com.jin10.spider.common.bean.BaseResponse;
import com.jin10.spider.common.exception.BaseException;
import com.jin10.spider.common.utils.PageUtils;
import com.jin10.spider.spiderserver.constants.DataCache;
import com.jin10.spider.spiderserver.entity.SpiderMessageMonitor;
import com.jin10.spider.spiderserver.entity.SysGroupMonitor;
import com.jin10.spider.spiderserver.form.SpiderMessageMonitorForm;
import com.jin10.spider.spiderserver.form.SysGroupMonitorFrom;
import com.jin10.spider.spiderserver.service.ISpiderMessageMonitorService;
import com.jin10.spider.spiderserver.service.ISysGroupMonitorService;
import com.jin10.spider.spiderserver.utils.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


/**
 * <p>
 * 消息来源监控 前端控制器
 * </p>
 *
 * @author Airey
 * @since 2019-12-17
 */
@RestController
@RequestMapping("/spider-message-monitor")
@Slf4j
public class SpiderMessageMonitorController {

    @Autowired
    private ISpiderMessageMonitorService monitorService;
    @Autowired
    private ISysGroupMonitorService sysGroupMonitorService;


    /**
     * 根据条件查询最后更新消息状态列表
     *
     * @param form
     * @return
     */
    @PostMapping("queryForList")
    public BaseResponse queryForList(@RequestBody SpiderMessageMonitorForm form) {
        log.info("开始根据条件查询监控消息列表！！！" + form);
        IPage<SpiderMessageMonitor> page = monitorService.queryForList(form);
        return BaseResponse.ok(new PageUtils(page));
    }


    /**
     * 查询对应的消息来源分页列表
     *
     * @param basePageRequest
     * @return
     */
    @PostMapping("list")
    public BaseResponse list(@RequestBody BasePageRequest basePageRequest) {

        IPage<SpiderMessageMonitor> list = monitorService.findList(basePageRequest);
        return BaseResponse.ok(new PageUtils(list));
    }


    /**
     * 根据分类查询
     *
     * @param form
     * @return
     */
    @PostMapping("/byCategory")
    public BaseResponse byCategory(@RequestBody SpiderMessageMonitorForm form) {
        IPage<SpiderMessageMonitor> spiderMessageMonitorList = monitorService.byCategory(form);
        return BaseResponse.ok(new PageUtils(spiderMessageMonitorList));
    }

    /**
     * 模糊查询包含channel或者source的记录
     *
     * @return
     */
    @PostMapping("/fuzzySearch")
    public BaseResponse search(@RequestBody SpiderMessageMonitorForm form) {
        IPage<SpiderMessageMonitor> list = monitorService.search(form);
        return BaseResponse.ok(new PageUtils(list));
    }


    /**
     * 更新是否自动推送标志
     *
     * @param form
     * @return
     */
    @PostMapping("updateAutoPushStatus")
    public BaseResponse updateAutoPushStatus(@RequestBody SpiderMessageMonitorForm form) {

        LambdaUpdateWrapper<SpiderMessageMonitor> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SpiderMessageMonitor::getId, form.getId()).set(SpiderMessageMonitor::getAutoPush, form.getAutoPush());
        boolean update = monitorService.update(null, updateWrapper);

        SpiderMessageMonitor spiderMessageMonitor = monitorService.getById(form.getId());
        String md5Key = MD5Util.getMessageMonitorCode(spiderMessageMonitor.getSource(), spiderMessageMonitor.getCategory(), spiderMessageMonitor.getChannel());
        String formatKey = StrUtil.format("{}:{}:{}", spiderMessageMonitor.getSource(), spiderMessageMonitor.getCategory(), spiderMessageMonitor.getChannel());
        if (form.getAutoPush().equals(0)) {
            DataCache.autoPushMap.put(md5Key, formatKey);
        } else {
            DataCache.autoPushMap.remove(md5Key);
        }
        log.info("DataCache.autoPushMap 更新后为 ==>  {}", DataCache.autoPushMap);

        return BaseResponse.ok(update);
    }

    /**
     * 设置源为隐藏
     * @return
     */
    @PostMapping("setHidden")
    public BaseResponse setHidden(@RequestBody SysGroupMonitorFrom sysGroupMonitorFrom){
        sysGroupMonitorService.setHidden(sysGroupMonitorFrom);
        return BaseResponse.ok();
    }

    /**
     * 设置源显示
     * @return
     */
    @PostMapping("setDisplay")
    public BaseResponse setDisplay(@RequestBody SysGroupMonitorFrom sysGroupMonitorFrom){
        sysGroupMonitorService.setDisplay(sysGroupMonitorFrom);
        return BaseResponse.ok();
    }

    /**
     * 设置源为隐藏
     * @return
     */
    @PostMapping("batchSetHidden")
    public BaseResponse batchSetHidden(@RequestBody List<SysGroupMonitorFrom> fromList){
        if(fromList == null || fromList.isEmpty()){
            throw new BaseException("参数错误");
        }
        sysGroupMonitorService.batchSetHidden(fromList);
        return BaseResponse.ok();
    }

}
