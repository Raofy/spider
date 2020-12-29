package com.jin10.spider.modules.statistics.bean;

import lombok.Data;

/**
 * @author hongda.fang
 * @date 2019-12-13 15:22
 * ----------------------------------------------
 * 生产队列警告
 */
@Data
public class TaskProductWarn extends BaseWarnBean {

    public TaskProductWarn(int intervalTime1, int intervalTime2, String type) {
        super(intervalTime1, intervalTime2, type);
    }


    private int curQueueSize;
}
