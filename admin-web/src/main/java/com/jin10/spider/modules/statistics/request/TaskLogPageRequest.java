package com.jin10.spider.modules.statistics.request;

import cn.hutool.core.date.DateUtil;
import com.jin10.spider.common.bean.BasePageRequest;
import lombok.Data;

import java.util.Date;

/**
 * @author hongda.fang
 * @date 2019-12-10 10:37
 * ----------------------------------------------
 * 请求参数类封装
 * ----------------------------------------------
 */
@Data
public class TaskLogPageRequest extends BasePageRequest {

    /**
     * 模板id
     */
    private Long tempId;

    /**
     * 任务Id
     */
    private String taskId;

    /**
     * 爬虫节点机器
     */
    private String ip;

    /**
     * 代理的IP加端口
     */
    private String proxyIp;

    /**
     * 代理ip
     */
    private String proxy;

    /**
     * 代理端口
     */
    private String proxyPort;

    /**
     * 代理地区
     */
    private String proxyArea;

    private String source;


    private Date curTime = new Date();
    /**
     * 开始时间 当前时间前 24 小时
     */
    private Long startTime = DateUtil.offsetDay(curTime, -2).getTime();

    /**
     * 结束时间
     */
    private Long endTime = curTime.getTime();

    /**
     * 状态 2-成功
     */
    private Integer status;


    /**
     * 维护人员账号
     */
    private String maintainer;


}
