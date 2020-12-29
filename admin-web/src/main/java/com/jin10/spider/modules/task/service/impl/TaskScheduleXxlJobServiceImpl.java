package com.jin10.spider.modules.task.service.impl;

import com.jin10.spider.modules.task.service.IProductTaskService;
import com.jin10.spider.modules.task.service.XxlJobService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Airey
 * @date 2020/1/14 18:10
 * ----------------------------------------------
 * 爬虫任务调度执行类，用xxl-job执行统一调度，支持分布式调度和单点调度
 * ----------------------------------------------
 */
@Service
public class TaskScheduleXxlJobServiceImpl implements XxlJobService {


    @Autowired
    private IProductTaskService productTaskService;


    /**
     * 执行xxl-job调度任务
     *
     * @param params
     * @return
     * @throws Exception
     */
    @XxlJob(value = "spiderAdminJobHandler")
    @Override
    public ReturnT<String> execute(String params) throws Exception {

        XxlJobLogger.log("开始生产任务！！！！");
        productTaskService.startProductTask();
        XxlJobLogger.log("生产任务成功！！！！");

        return ReturnT.SUCCESS;
    }






}
