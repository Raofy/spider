package com.jin10.spider.modules.statistics.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author Airey
 * @date 2020/5/14 11:26
 * ----------------------------------------------
 * 模板统计结果
 * ----------------------------------------------
 */
@Data
public class TemplateResultDto {

    /**
     * 模板id
     */
    private Long tempId;

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


}
