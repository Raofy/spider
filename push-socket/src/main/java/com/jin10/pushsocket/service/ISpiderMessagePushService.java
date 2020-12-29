package com.jin10.pushsocket.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jin10.pushsocket.entity.SpiderMessagePush;

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
