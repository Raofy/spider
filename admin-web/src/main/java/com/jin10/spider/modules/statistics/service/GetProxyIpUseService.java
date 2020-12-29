package com.jin10.spider.modules.statistics.service;

import cn.hutool.json.JSONUtil;
import com.jin10.spider.common.annotation.ActionCode;
import com.jin10.spider.common.constants.ActionCodeConstants;
import com.jin10.spider.common.netty.global.ChannelSupervise;
import com.jin10.spider.common.netty.interf.IActionSocketService;
import com.jin10.spider.modules.statistics.request.GetIpProxyUseRequest;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

/**
 * @author hongda.fang
 * @date 2019-12-11 10:50
 * ----------------------------------------------
 * <p>
 * 订阅获取 ip 信息
 */
@Component
@ActionCode(value = ActionCodeConstants.GET_PROXY_IP_USE)
public class GetProxyIpUseService implements IActionSocketService {


    @Override
    public Object doAction(ChannelHandlerContext context, String message) {
        GetIpProxyUseRequest request = JSONUtil.toBean(message, GetIpProxyUseRequest.class);
        if (request.isSubscribe()){
            ChannelSupervise.addChannelByObject(context.channel(), request);
        }else{
            ChannelSupervise.removeByObjectChannel(context.channel(), request);
        }
        return null;
    }
}
