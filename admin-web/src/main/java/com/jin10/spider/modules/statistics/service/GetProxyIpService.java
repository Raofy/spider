package com.jin10.spider.modules.statistics.service;

import com.jin10.spider.common.annotation.ActionCode;
import com.jin10.spider.common.annotation.SocketResponseBody;
import com.jin10.spider.common.bean.MsgResponse;
import com.jin10.spider.common.constants.ActionCodeConstants;
import com.jin10.spider.common.enums.MsgCodeEnum;
import com.jin10.spider.common.netty.global.ChannelSupervise;
import com.jin10.spider.common.netty.interf.IActionSocketService;
import com.jin10.spider.modules.task.service.IIpProxyProcessService;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author hongda.fang
 * @date 2019-12-11 10:50
 * ----------------------------------------------
 *
 * 订阅获取 ip 信息
 */
@Component
@ActionCode(value = ActionCodeConstants.GET_PROXY_IP)
public class GetProxyIpService implements IActionSocketService {

    @Autowired
    private IIpProxyProcessService iIpProxyProcessService;

    @SocketResponseBody
    @Override
    public Object doAction(ChannelHandlerContext context, String message) {
        ChannelSupervise.addChannelByActionCode(context.channel(), ActionCodeConstants.GET_PROXY_IP);
//        MsgResponse msgResponse = new MsgResponse(MsgCodeEnum.PROXY_IP_LIST, iIpProxyProcessService.groupAreaIpInfo());
        MsgResponse msgResponse = new MsgResponse(MsgCodeEnum.PROXY_IP_LIST, null);
        return msgResponse;
    }
}
