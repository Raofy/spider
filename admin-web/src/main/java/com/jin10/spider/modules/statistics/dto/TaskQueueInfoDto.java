package com.jin10.spider.modules.statistics.dto;

import lombok.Data;

/**
 * @author hongda.fang
 * @date 2019-12-11 16:53
 * ----------------------------------------------
 * web 端监控 任务队列
 */
@Data
public class TaskQueueInfoDto {


    private int taskSize;

    private boolean isForeign;


}
