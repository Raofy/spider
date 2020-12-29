package com.jin10.spider.modules.statistics.service;


import com.jin10.spider.common.utils.PageUtils;
import com.jin10.spider.modules.statistics.bean.TaskLog;
import com.jin10.spider.modules.statistics.dto.ProxyResultDto;
import com.jin10.spider.modules.statistics.dto.SpiderRunResultDto;
import com.jin10.spider.modules.statistics.dto.TemplateResultDto;
import com.jin10.spider.modules.statistics.request.TaskLogPageRequest;

import java.util.Date;
import java.util.List;

/**
 * @author hongda.fang
 * @date 2019-11-26 13:43
 * ----------------------------------------------
 */

public interface ITaskLogService {


    TaskLog findByTaskId(String taskId);

    /**
     * 统计爬虫服务器节点当天(最近24小时)抓取任务统计结果
     *
     * @param request
     * @return
     */
    List<SpiderRunResultDto> countSpiderRunResult(TaskLogPageRequest request);

    /**
     * 统计 代理ip 当天(最近24小时)抓取任务统计结果
     *
     * @param request
     * @return
     */
    List<ProxyResultDto> countProxy(TaskLogPageRequest request);

    /**
     * 统计爬虫模板当天(最近24小时)抓取任务统计结果
     *
     * @param request
     * @return
     */
    TemplateResultDto countTemplateResult(TaskLogPageRequest request);


    PageUtils queryPage(TaskLogPageRequest tempIdRequest);

    /**
     * 清除数据
     *
     * @param time *之前的数据
     * @return
     */
    boolean deleteLog(Date time);

    /**
     * 清理停止状态的数据
     *
     * @param time
     * @return
     */
    boolean deleteStopTemplateLog(Date time);


}
