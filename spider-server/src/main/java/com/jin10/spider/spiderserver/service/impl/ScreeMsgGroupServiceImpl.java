package com.jin10.spider.spiderserver.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jin10.spider.common.annotation.ActionCode;
import com.jin10.spider.common.annotation.SocketResponseBody;
import com.jin10.spider.common.constants.ActionCodeConstants;
import com.jin10.spider.common.netty.global.ChannelSupervise;
import com.jin10.spider.common.netty.interf.IActionSocketService;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author Airey
 * @date 2020/3/5 15:04
 * ----------------------------------------------
 * 分拣消息分组请求
 * ----------------------------------------------
 */
@Service
@ActionCode(ActionCodeConstants.SCREEN_MSG_GROUP)
@Slf4j
public class ScreeMsgGroupServiceImpl implements IActionSocketService {

    @Override
    @SocketResponseBody
    public Object doAction(ChannelHandlerContext context, String message) {

        log.info("开始处理业务请求消息 : " + message);
        JSONObject jsonObject = JSONObject.parseObject(message);
        String machineCode = jsonObject.getString("machineCode");
        if (StringUtils.isBlank(machineCode)) {
            log.error("警告！！！机器码为空,加入消息分拣列表失败！！！");
            return null;
        }
        ChannelSupervise.addChannelByObject(context.channel(), machineCode);
        String noticeMsg = "加入分拣消息列表成功！！！";
        return noticeMsg;

    }
}
