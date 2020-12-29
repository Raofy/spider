package com.jin10.pushsocket.service.impl;


import com.jin10.pushsocket.annotation.ActionCode;
import com.jin10.pushsocket.annotation.SocketResponseBody;
import com.jin10.pushsocket.bean.MsgResponse;
import com.jin10.pushsocket.constants.ActionCodeConstants;
import com.jin10.pushsocket.enums.MsgCodeEnum;
import com.jin10.pushsocket.interf.IActionSocketService;
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
