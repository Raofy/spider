package com.jin10.spider.modules.statistics.service;

import com.jin10.spider.common.annotation.ActionCode;
import com.jin10.spider.common.annotation.SocketResponseBody;
import com.jin10.spider.common.bean.MsgResponse;
import com.jin10.spider.common.constants.ActionCodeConstants;
import com.jin10.spider.common.enums.MsgCodeEnum;
import com.jin10.spider.common.netty.global.ChannelSupervise;
import com.jin10.spider.common.netty.interf.IActionSocketService;
import com.jin10.spider.modules.statistics.bean.ServerInfo;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author hongda.fang
 * @date 2019-12-11 10:50
 * ----------------------------------------------
 */
@Component
@ActionCode(value = ActionCodeConstants.GET_SERVER_INFOS)
public class GetServerInfoService implements IActionSocketService {

    @Autowired
    private ServerInfoService serverInfoService;

    @SocketResponseBody
    @Override
    public Object doAction(ChannelHandlerContext context, String message) {
        ChannelSupervise.addChannelByActionCode(context.channel(), ActionCodeConstants.GET_SERVER_INFOS);
        List<ServerInfo> serverInfos = serverInfoService.getServerInfos();
        MsgResponse msgResponse = new MsgResponse(MsgCodeEnum.SERVER_INFOS, serverInfos);
        return msgResponse;
    }
}
