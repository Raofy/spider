package com.jin10.spider.spiderserver.service;

import com.alibaba.fastjson.JSONObject;
import com.jin10.spider.spiderserver.entity.SpiderMessage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 爬虫消息列表 服务类
 * </p>
 *
 * @author Airey
 * @since 2019-11-18
 */
public interface ISpiderMessageService extends IService<SpiderMessage> {


    /**
     * 批量插入数据库
     *
     * @param spiderMessageList
     * @return
     */
    boolean insertBatch(List<SpiderMessage> spiderMessageList);


    /**
     * 插入单条数据库记录
     *
     * @param spiderMessage
     * @return
     */
    boolean insert(SpiderMessage spiderMessage);


    /**
     * 获取最新的数据
     * @return
     */
    JSONObject findLatestMessageList();


    /**
     * 查询需要返回给前端的补偿消息
     * @param time
     * @return
     */
    List<SpiderMessage> findCompleteMessageList(Long time);


}
