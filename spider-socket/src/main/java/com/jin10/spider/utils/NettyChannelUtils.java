package com.jin10.spider.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.jin10.spider.common.bean.MsgResponse;
import com.jin10.spider.common.enums.MsgCodeEnum;
import com.jin10.spider.common.netty.global.ChannelSupervise;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author Airey
 * @date 2020/2/7 15:17
 * ----------------------------------------------
 * Netty推送消息工具类
 * ----------------------------------------------
 */
@Service
@Slf4j
public class NettyChannelUtils {


    /**
     * 广播 订阅的 action 的渠道
     *
     * @param registerAction
     * @param codeEnum
     * @param entity
     */
    public void pushRegisterActionChannel(int registerAction, MsgCodeEnum codeEnum, Object entity) {
        /**
         * 检查该去掉是否有 ChannelGroup，有则发送
         */
        if (ChannelSupervise.existActionChannelGroup(registerAction)) {
            MsgResponse msgResponse = new MsgResponse(codeEnum, entity);
            ChannelSupervise.send2AllAction(registerAction, msgResponse);
        }
    }

    /**
     * 广播 订阅的 action 的渠道
     *
     * @param registerAction
     * @param msgResponse
     */
    public void pushRegisterActionChannel(int registerAction, MsgResponse msgResponse) {
        /**
         * 检查该去掉是否有 ChannelGroup，有则发送
         */
        if (ChannelSupervise.existActionChannelGroup(registerAction)) {
            ChannelSupervise.send2AllAction(registerAction, msgResponse);
        }
    }

    /**
     * 广播 订阅的 action 的渠道
     *
     * @param registerAction
     * @param msgResponse
     */
    public void pushRegisterActionChannel(int registerAction, String msgResponse) {
        /**
         * 检查该去掉是否有 ChannelGroup，有则发送
         */
        if (ChannelSupervise.existActionChannelGroup(registerAction)) {
            ChannelSupervise.send2AllAction(registerAction, msgResponse);
        }
    }

    /**
     * 根据tempId推送爬虫日志消息
     *
     * @param registerAction
     * @param tempId
     * @param msgResponse
     */
    public void pushRegisterByTempId(int registerAction, Integer tempId, String msgResponse) {
        /**
         * 检查该去掉是否有 Channel，有则发送
         */

        Map<Integer, Channel> integerChannelMap = ChannelSupervise.actionTempIdGlobalGroupMap.get(registerAction);
        if (CollUtil.isNotEmpty(integerChannelMap)) {
            Channel channel = integerChannelMap.get(tempId);
            if (ObjectUtil.isNotNull(channel)) {
                channel.writeAndFlush(new TextWebSocketFrame(msgResponse));
            }
        }


    }


    /**
     * 根据ip推送爬虫日志消息
     *
     * @param registerAction
     * @param pythonIp
     * @param msgResponse
     */
    public void pushRegisterByIp(int registerAction, String pythonIp, String msgResponse) {
        /**
         * 检查该去掉是否有 Channel，有则发送
         */

        Map<String, Channel> ipChannelMap = ChannelSupervise.actionIpGlobalGroupMap.get(registerAction);
//        log.info("channelMap: " + ChannelSupervise.actionIpGlobalGroupMap);
        if (CollUtil.isNotEmpty(ipChannelMap)) {
            Channel channel = ipChannelMap.get(pythonIp);
            if (ObjectUtil.isNotNull(channel)) {
                channel.writeAndFlush(new TextWebSocketFrame(msgResponse));
            }
        }


    }


}
