package com.jin10.spider.common.netty.global;

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @author Airey
 * @date 2020/1/16 17:09
 * ----------------------------------------------
 * 客户端全局channel
 * ----------------------------------------------
 */
public class ClientChannelGroup {

    /**
     * 记录所有加入websocket服务器的客户端地址
     */
    public static ChannelGroup clientGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

}
