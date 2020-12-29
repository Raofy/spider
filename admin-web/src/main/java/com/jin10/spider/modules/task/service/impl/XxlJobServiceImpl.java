package com.jin10.spider.modules.task.service.impl;

import cn.hutool.core.date.DateUtil;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author Airey
 * @date 2020/1/14 15:01
 * ----------------------------------------------
 * 测试调度任务
 * ----------------------------------------------
 */
@Service
public class XxlJobServiceImpl {

    private static Logger logger = LoggerFactory.getLogger(XxlJobServiceImpl.class);

    @XxlJob("spiderJobHandler")
    public ReturnT<String> execute(String params) throws Exception {

        XxlJobLogger.log("开始执行爬虫调度任务！！！！");

        System.out.println(DateUtil.date(new Date()) + "爬虫调度任务开始执行了！！！");

        System.out.println(DateUtil.date() + "爬虫调度任务执行成功了！");

        XxlJobLogger.log("执行爬虫调度任务成功！！！！");

        return ReturnT.SUCCESS;
    }


}
