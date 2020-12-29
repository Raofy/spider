package com.jin10.spider.spiderserver.service.impl;

import com.jin10.spider.common.annotation.ActionCode;
import com.jin10.spider.common.annotation.SocketResponseBody;
import com.jin10.spider.common.bean.MsgResponse;
import com.jin10.spider.common.constants.ActionCodeConstants;
import com.jin10.spider.common.enums.MsgCodeEnum;
import com.jin10.spider.common.netty.interf.IActionSocketService;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Airey
 * @date 2020/3/20 14:33
 * ----------------------------------------------
 * 心跳响应机制
 * ----------------------------------------------
 */
@ActionCode(ActionCodeConstants.HEART_BEAT)
@Service
@Slf4j
public class HeartBeatServiceImpl implements IActionSocketService {

    @Override
    @SocketResponseBody
    public Object doAction(ChannelHandlerContext context, String message) {
        Map<String, Object> pongMap = new HashMap<>();
        log.info("ping , ip = {}", context.channel().remoteAddress());
        pongMap.put("msg", "pong");
        return new MsgResponse(MsgCodeEnum.HEART, pongMap);
    }
}
