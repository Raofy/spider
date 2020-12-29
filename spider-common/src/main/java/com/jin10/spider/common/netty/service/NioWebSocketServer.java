package com.jin10.spider.common.netty.service;

import com.jin10.spider.common.netty.handler.NioWebSocketChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author zxlei
 * @date 2020/4/22  15:09
 * ----------------------------------------------
 * websocket服务端实现
 * ----------------------------------------------
 */
@Component
@Slf4j
public class NioWebSocketServer {


    @Autowired
    private NioWebSocketChannelInitializer channelInitializer;



    public void init(int inetPort) {
        log.info("正在启动websocket服务器");

        NioEventLoopGroup boss = new NioEventLoopGroup(1, new DefaultThreadFactory("boss"));
        NioEventLoopGroup work = new NioEventLoopGroup(0, new DefaultThreadFactory("work"));

        log.info("work Thread Size : " + work.executorCount());
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, work);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(channelInitializer);
            bootstrap.childOption(NioChannelOption.TCP_NODELAY, true);
            bootstrap.option(NioChannelOption.SO_BACKLOG, 1024);
            Channel channel = bootstrap.bind(inetPort).sync().channel();
            log.info("webSocket服务器启动成功：" + channel);
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.info("运行出错：" + e);
        } finally {
            boss.shutdownGracefully();
            work.shutdownGracefully();
            log.info("websocket服务器已关闭");
        }
    }

    public static void main(String[] args) {
        new NioWebSocketServer().init(8088);
    }
}
