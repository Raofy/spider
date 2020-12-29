package com.jin10.spider.spiderserver.form;

import lombok.Data;

import java.util.Date;

/**
 * @author Airey
 * @date 2019/11/27 11:04
 * ----------------------------------------------
 * ES搜索条件封装
 * ----------------------------------------------
 */
@Data
public class SpiderMessageSearchForm {

    /**
     * 标题
     */
    private String title;

    /**
     * 来源
     */
    private String source;

    /**
     * 类别
     */
    private String category;

    /**
     * 频道
     */
    private String channel;

    /**
     * 网址
     */
    private String pageSite;

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 模板id
     */
    private Integer tempId;

    /**
     * 查询第几页
     */
    private Integer pageNum;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 每页的数据条数
     */
    private Integer pageSize;

    /**
     * 是否展示隐藏的源
     */
    private boolean whetherToShowOrHide = true;


}
