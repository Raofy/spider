package com.jin10.spider.modules.statistics.service;

import cn.hutool.json.JSONUtil;
import com.jin10.spider.common.annotation.ActionCode;
import com.jin10.spider.common.constants.ActionCodeConstants;
import com.jin10.spider.common.netty.interf.IActionSocketService;
import com.jin10.spider.common.request.SocketActiveRequest;
import com.jin10.spider.modules.task.service.UrlTaskProcessService;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author hongda.fang
 * @date 2019-11-08 16:27
 * ----------------------------------------------
 *
 * 爬虫上下线
 */
@Component
@ActionCode(value = ActionCodeConstants.SOCKET_ACTIVE)
public class SocketActiveService implements IActionSocketService {

    @Autowired
    private UrlTaskProcessService taskProcessService;

    @Override
    public Object doAction(ChannelHandlerContext context, String message) {
        SocketActiveRequest socketActiveRequest = JSONUtil.toBean(message, SocketActiveRequest.class);

        return null;
    }
}
