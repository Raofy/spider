package com.jin10.spider.modules.statistics.service;


import com.alibaba.fastjson.JSONArray;
import com.jin10.spider.modules.statistics.bean.ServerInfoWarn;
import com.jin10.spider.modules.statistics.bean.TaskLog;
import com.jin10.spider.modules.statistics.bean.TaskProductWarn;
import com.jin10.spider.modules.statistics.dto.SpiderRunResultDto;
import com.jin10.spider.modules.template.entity.Template;

import java.util.Date;


/**
 * @author hongda.fang
 * @date 2019-11-29 10:51
 * ----------------------------------------------
 * 钉钉通知，报警所有类型的警告归类管理
 */
public interface IDingTalkWarnService {

    /**
     * 通用消息警告
     *
     * @param msg
     */
    void msgWarn(String msg);

    /**
     * 密钥警告消息
     *
     * @param msg
     */
    void secretKeyMsg(String msg);


    /**
     * 任务处理失败次数警告 面板形式
     *
     * @param log
     * @param template
     */
    void taskFailModuleWarn(TaskLog log, Template template);


    /**
     * 任务处理失败次数警告 markdown形式
     *
     * @param log
     * @param template
     */
    void taskFailMarkDownWarn(TaskLog log, Template template);

    /**
     * 服务器负荷警告
     *
     * @param serverInfoWarn
     */
    void serverWarn(ServerInfoWarn serverInfoWarn);

    /**
     * 曾经上报过的服务器，如果超过2分钟没有上报数据，提醒 请检查服务器是否被释放
     */
    void timeOutServerWarn(ServerInfoWarn serverInfoWarn);


    /**
     * 生产队列警告
     *
     * @param taskProductWarn
     */
    void taskProductWarn(TaskProductWarn taskProductWarn);


    /**
     * 每小时汇报爬虫任务执行统计情况
     *
     * @param spiderRunResultDtoList
     */
    void taskLogCountWarn(SpiderRunResultDto spiderRunResultDtoList);

    /**
     * 通知维护人员对应的任务情况
     *
     * @param jsonArray
     * @param endTime
     * @param startTime
     */
    void warnMaintainer(JSONArray jsonArray, Date startTime, Date endTime);


    /**
     * 维护人员上报维护信息
     *
     * @param maintainer
     */
    void warnfinishMaintainTask(String maintainer);


}
