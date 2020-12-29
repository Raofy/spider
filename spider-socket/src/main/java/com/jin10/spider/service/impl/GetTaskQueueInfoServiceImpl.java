package com.jin10.spider.service.impl;

import com.jin10.spider.common.annotation.ActionCode;
import com.jin10.spider.common.constants.ActionCodeConstants;
import com.jin10.spider.common.netty.global.ChannelSupervise;
import com.jin10.spider.common.netty.interf.IActionSocketService;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Airey
 * @date 2020/2/10 10:07
 * ----------------------------------------------
 * 监控队列消息
 * ----------------------------------------------
 */
@Service
@ActionCode(value = ActionCodeConstants.TASK_QUEUE_INFO)
@Slf4j
public class GetTaskQueueInfoServiceImpl implements IActionSocketService {



    @Override
    public Object doAction(ChannelHandlerContext context, String message) {
        ChannelSupervise.addChannelByActionCode(context.channel(), ActionCodeConstants.TASK_QUEUE_INFO);
        return null;
    }
}
