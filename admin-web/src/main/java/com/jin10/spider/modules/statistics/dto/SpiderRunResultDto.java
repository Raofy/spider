package com.jin10.spider.modules.statistics.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author hongda.fang
 * @date 2019-12-10 11:48
 * ----------------------------------------------
 */
@Data
public class SpiderRunResultDto {

    private String ip;

    /**
     * 爬取总任务量
     */
    private long all;
    /**
     * 成功数量
     */
    private long success;
    /**
     * 失败数量
     */
    private long fail;

    /**
     * 成功率
     */
    private double successRate;

    /**
     * 统计的开始时间
     */
    private Date startTime;

    /**
     * 统计的结束时间
     */
    private Date endTime;

    /**
     * 当天爬取网站数量
     */
    private long temp;


}
