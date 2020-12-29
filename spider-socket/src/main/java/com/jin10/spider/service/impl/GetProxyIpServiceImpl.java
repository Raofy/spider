package com.jin10.spider.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.jin10.spider.common.annotation.ActionCode;
import com.jin10.spider.common.annotation.SocketResponseBody;
import com.jin10.spider.common.constants.ActionCodeConstants;
import com.jin10.spider.common.netty.global.ChannelSupervise;
import com.jin10.spider.common.netty.interf.IActionSocketService;
import com.jin10.spider.utils.NettyChannelUtils;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Airey
 * @date 2020/3/4 23:40
 * ----------------------------------------------
 * 获取代理ip池中的Ip
 * ----------------------------------------------
 */
@Service
@ActionCode(value = ActionCodeConstants.GET_PROXY_IP)
public class GetProxyIpServiceImpl implements IActionSocketService {


    @Autowired
    private NettyChannelUtils nettyChannelUtils;

    @Override
    @SocketResponseBody
    public Object doAction(ChannelHandlerContext context, String message) {

        JSONObject jsonObj = JSONObject.parseObject(message);

        if (jsonObj.containsKey("clientMsg")) {
            String clientMsg = jsonObj.getString("clientMsg");
            nettyChannelUtils.pushRegisterActionChannel(ActionCodeConstants.GET_PROXY_IP, clientMsg);
            return null;
        } else {
            ChannelSupervise.addChannelByActionCode(context.channel(), ActionCodeConstants.GET_PROXY_IP);
            String noticeMsg = "加入获取ip实时消息分组成功！！！";
            return noticeMsg;
        }


    }
}
