package com.jin10.spider.spiderserver.service.impl;

import com.jin10.spider.common.annotation.ActionCode;
import com.jin10.spider.common.annotation.SocketResponseBody;
import com.jin10.spider.common.constants.ActionCodeConstants;
import com.jin10.spider.common.netty.global.ChannelSupervise;
import com.jin10.spider.common.netty.interf.IActionSocketService;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Airey
 * @date 2020/3/5 14:24
 * ----------------------------------------------
 * 正常转发爬虫消息分组请求
 * ----------------------------------------------
 */
@Service
@ActionCode(ActionCodeConstants.NORMAL_MSG_GROUP)
@Slf4j
public class NormalMsgGroupServiceImpl implements IActionSocketService {


    @Override
    @SocketResponseBody
    public Object doAction(ChannelHandlerContext context, String message) {

        log.info("开始处理业务请求消息 : " + message);
        ChannelSupervise.addChannelByActionCode(context.channel(), ActionCodeConstants.NORMAL_MSG_GROUP);
        String noticeMsg = "加入分组成功！开始接收正常转发爬虫消息！！！";
        return noticeMsg;
    }
}
