package com.jin10.spider.modules.statistics.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jin10.spider.common.utils.Constant;
import com.jin10.spider.modules.task.entity.IpInfo;
import com.jin10.spider.modules.task.listener.ConnectionListener;
import com.jin10.spider.modules.task.service.IIpInfoService;
import com.jin10.spider.modules.task.service.IIpProxyProcessService;
import com.jin10.spider.modules.template.service.ITemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @author hongda.fang
 * @date 2019-12-13 16:27
 * ----------------------------------------------
 */

@Component
public class ScheduledTask {

    private Logger logger = LoggerFactory.getLogger(getClass());


    @Autowired
    private ServerInfoService infoService;
    @Autowired
    private IIpProxyProcessService proxyProcessService;
    @Autowired
    private ITemplateService templateService;
    @Autowired
    private ITaskLogService taskLogService;
    @Autowired
    private ConnectionListener connectionListener;
    @Autowired
    private IIpInfoService ipInfoService;


    /**
     * 重连websocket操作
     */
    @Scheduled(cron = "0/10 * * * * ? ")
    public void reConnetion() {
        connectionListener.reConnection();
    }


    /**
     * 间隔5秒执行 检查爬虫服务
     */
    @Scheduled(cron = "0/10 * * * * ? ")
    public void checkTimeOutServerWarn() {
        logger.warn("======   checkTimeOutServerWarn   ======");
        infoService.checkTimeOutServerWarn();
    }


    /**
     * 检查代理Ip池 高质量
     */
    @Scheduled(cron = "30/1 * * * * ? ")
    public void checkIp() {
        logger.warn("======   checkHighIp   ======");
        proxyProcessService.checkIp(Constant.TEMPLATE.PROXY_LEVEL_HIGH, false);
    }


    /**
     * 清理es日志  每天1点执行
     */
    @Scheduled(cron = "0 0 12 * * ?")
    public void clearEsLog() {
        logger.warn("==== clearEsLog ======");
        DateTime dateTime = DateUtil.offsetDay(new Date(), -2);
        DateTime dateTimeStopStatus = DateUtil.offsetDay(new Date(), -3);
        infoService.deleteLog(dateTime);
        taskLogService.deleteLog(dateTime);
        taskLogService.deleteStopTemplateLog(dateTimeStopStatus);
    }


    /**
     * 模版定时过期
     */
    @Scheduled(cron = "0/10 * * * * ? ")
    public void updateStatusExpireToStop() {
        logger.warn("======   update temp Status Expire To Stop   ======");
        int i = templateService.updateStatusExpireToStop();
        if (i > 0) {
            templateService.initTemplate();
            logger.warn(" has update ");
        }
    }

    /**
     * 每10分钟检测一次vps代理是否合法
     */
    @Scheduled(cron = "0 0/10 * * * ? ")
    public void checkVpsProxy() {
        logger.warn("======= 开始检测Vps是否合法 ======== ");
        LambdaQueryWrapper<IpInfo> queryWrapper = new LambdaQueryWrapper<>();
        DateTime dateTime = DateUtil.offsetMinute(new Date(), -6);
        queryWrapper.eq(IpInfo::isWheVaild, true);
        queryWrapper.eq(IpInfo::getPlatform, "vps");
        queryWrapper.lt(IpInfo::getCreateTime, dateTime);
        List<IpInfo> list = ipInfoService.list(queryWrapper);
        if (CollUtil.isNotEmpty(list)) {
            logger.info("检测到不合法的Vps == > {}", list);
            list.forEach(item -> proxyProcessService.updateIpQueue(item));
        } else {
            logger.info("未检测到非法的Vps,情况正常");
        }

    }


}
