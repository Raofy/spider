package com.jin10.spider.modules.task.service;

import com.xxl.job.core.biz.model.ReturnT;

/**
 * @author Airey
 * @date 2020/1/14 18:08
 * ----------------------------------------------
 * XXL-JOB任务调度接口
 * ----------------------------------------------
 */
public interface XxlJobService {


    /**
     * 执行xxl-job调度任务
     * @param params
     * @return
     * @throws Exception
     */
    ReturnT<String> execute(String params) throws Exception;


}
