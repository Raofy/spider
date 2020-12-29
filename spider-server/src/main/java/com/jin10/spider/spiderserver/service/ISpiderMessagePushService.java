package com.jin10.spider.spiderserver.service;

import com.jin10.spider.spiderserver.entity.SpiderMessagePush;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 推送爬虫消息列表 服务类
 * </p>
 *
 * @author Airey
 * @since 2020-03-05
 */
public interface ISpiderMessagePushService extends IService<SpiderMessagePush> {


    /**
     * 清空列表
     */
    void deleteAll();

}
