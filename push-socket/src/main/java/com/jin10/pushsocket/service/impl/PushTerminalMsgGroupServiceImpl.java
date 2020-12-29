package com.jin10.pushsocket.service.impl;

import com.jin10.pushsocket.annotation.ActionCode;

import com.jin10.pushsocket.annotation.SocketResponseBody;
import com.jin10.pushsocket.constants.ActionCodeConstants;
import com.jin10.pushsocket.global.ChannelSupervise;
import com.jin10.pushsocket.interf.IActionSocketService;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Airey
 * @date 2020/3/5 15:09
 * ----------------------------------------------
 * 推送消息分组请求
 * ----------------------------------------------
 */
@Service
@ActionCode(ActionCodeConstants.PUSH_MSG_GROUP)
@Slf4j
public class PushTerminalMsgGroupServiceImpl implements IActionSocketService {


    @Override
    @SocketResponseBody
    public Object doAction(ChannelHandlerContext context, String message) {

        log.info("开始处理业务请求消息 : " + message);
        ChannelSupervise.addChannelByActionCode(context.channel(), ActionCodeConstants.PUSH_MSG_GROUP);
        String noticeMsg = "加入分组成功！开始接收推送的爬虫消息！！！";
        return noticeMsg;

    }
}
