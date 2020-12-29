package com.jin10.pushsocket.utils;


import com.jin10.pushsocket.bean.MsgResponse;
import com.jin10.pushsocket.enums.MsgCodeEnum;
import com.jin10.pushsocket.global.ChannelSupervise;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;
import java.net.InetSocketAddress;

/**
 * @author hongda.fang
 * @date 2019-11-22 17:22
 * ----------------------------------------------
 */
@Component
public class NettyUtils {


    public static String getClientIP(ChannelHandlerContext ctx) {
        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIP = insocket.getAddress().getHostAddress();
        return clientIP;
    }




    /**
     * 广播 订阅的 action 的渠道  通过fastJSON格式化
     *
     * @param registerAction
     * @param codeEnum
     * @param entity
     * @param fastJson
     */
    public void pushRegisterActionChannel(int registerAction, MsgCodeEnum codeEnum, Object entity, boolean fastJson) {
        /**
         * 检查该去掉是否有 ChannelGroup，有则发送
         */
        if (ChannelSupervise.existActionChannelGroup(registerAction)) {
            MsgResponse msgResponse = new MsgResponse(codeEnum, entity);
            ChannelSupervise.send2AllAction(registerAction, msgResponse, fastJson);
        }
    }


    /**
     * 通过机器码推送
     *
     * @param machineCode
     * @param codeEnum
     * @param entity
     * @param fastJson
     */
    public void pushMachineCodeActionChannel(Object machineCode, MsgCodeEnum codeEnum, Object entity, boolean fastJson) {

        if (ChannelSupervise.existObjectChannelGroup(machineCode)) {
            MsgResponse msgResponse = new MsgResponse(codeEnum, entity);
            ChannelSupervise.send2AllAction(machineCode, msgResponse, fastJson);
        }

    }


}
