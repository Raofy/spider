package com.jin10.spider.modules.task.listener;

import cn.hutool.core.collection.CollUtil;
import com.jin10.spider.common.netty.client.WebSocketClient;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.jin10.spider.common.netty.global.ClientChannelGroup.clientGroup;

/**
 * @author Airey
 * @date 2020/2/20 15:23
 * ----------------------------------------------
 * webSocket断线重连机制
 * ----------------------------------------------
 */
@Service
@Slf4j
public class ConnectionListener implements ChannelFutureListener {


    @Autowired
    private WebSocketClient webSocketClient;

    @Value("${custom.socket.serverUrl}")
    private String url;

    @Override
    public void operationComplete(ChannelFuture channelFuture) throws Exception {
        if (!channelFuture.isSuccess()) {
            final EventLoop loop = channelFuture.channel().eventLoop();
            loop.schedule(() -> {
                log.error("服务端链接不上，开始重连操作！ operationComplete");
                try {
                    webSocketClient.startClient(url);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, 3, TimeUnit.SECONDS);
        } else {
            log.info("服务端链接成功...");
        }
    }


    /**
     * 重连websocket监测
     */
    public void reConnection() {
        log.info("clientGroup : " + clientGroup);
        if (CollUtil.isEmpty(clientGroup)) {
            log.error("服务器链接不上，开始重连操作......");
            webSocketClient.startClient(url);
        }

    }


}
