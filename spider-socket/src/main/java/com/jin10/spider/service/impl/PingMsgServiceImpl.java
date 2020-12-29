package com.jin10.spider.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jin10.spider.common.annotation.ActionCode;
import com.jin10.spider.common.constants.ActionCodeConstants;
import com.jin10.spider.common.netty.interf.IActionSocketService;
import com.jin10.spider.common.request.BaseSocketRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Airey
 * @date 2020/5/12 10:21
 * ----------------------------------------------
 * ping 消息检测
 * ----------------------------------------------
 */
@ActionCode(value = ActionCodeConstants.PYTHON_PING_MSG)
@Service
@Slf4j
public class PingMsgServiceImpl implements IActionSocketService {


    @Override
    public Object doAction(ChannelHandlerContext context, String message) {

        log.info("收到python端心跳消息检测 ~~~" + message);
        BaseSocketRequest ping = JSONObject.toJavaObject(JSONObject.parseObject(message), BaseSocketRequest.class);
        BaseSocketRequest baseSocketRequest=new BaseSocketRequest();
        baseSocketRequest.setAction(ActionCodeConstants.PYTHON_PONG_MSG);
        baseSocketRequest.setIp(ping.getIp());

        context.channel().writeAndFlush(new TextWebSocketFrame(JSONObject.toJSONString(baseSocketRequest)));
        return null;
    }
}
