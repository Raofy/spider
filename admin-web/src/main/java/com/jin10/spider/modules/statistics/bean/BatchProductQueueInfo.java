package com.jin10.spider.modules.statistics.bean;

import com.jin10.spider.common.bean.UrlTask;
import lombok.Data;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hongda.fang
 * @date 2019-11-29 16:20
 * ----------------------------------------------
 *
 * 组合生产的 任务队列信息
 */
@Data
public class BatchProductQueueInfo {

    /**
     * 爬虫获取任务的时间
     */
    private ConcurrentHashMap<String, Date> getTaskMap;

    private ConcurrentHashMap<String, UrlTask> productTaskMap;

    private int times;

    private int curQueueSize;

    private int curForeignQueueSize;
}
