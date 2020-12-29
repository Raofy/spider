package com.jin10.pushsocket.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jin10.pushsocket.entity.SpiderMessagePush;
import com.jin10.pushsocket.mapper.SpiderMessagePushMapper;
import com.jin10.pushsocket.service.ISpiderMessagePushService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 推送爬虫消息列表 服务实现类
 * </p>
 *
 * @author Airey
 * @since 2020-03-05
 */
@Service
public class SpiderMessagePushServiceImpl extends ServiceImpl<SpiderMessagePushMapper, SpiderMessagePush> implements ISpiderMessagePushService {


    /**
     * 清空列表
     */
    @Override
    public void deleteAll() {

    }
}
