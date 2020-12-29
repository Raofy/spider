package com.jin10.spider.common.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Airey
 * @date 2020/1/16 16:59
 * ----------------------------------------------
 * webSocket客户端
 * ----------------------------------------------
 */
@Component
@Slf4j
public class WebSocketClient {

    /**
     * 启动socket客户端
     *
     * @param url
     */
    public void startClient(String url) {
        try {
            URI uri = new URI(url);

            String scheme = uri.getScheme() == null ? "ws" : uri.getScheme();
            final String host = uri.getHost() == null ? "127.0.0.1" : uri.getHost();
            final int port;
            if (uri.getPort() == -1) {
                if ("ws".equalsIgnoreCase(scheme)) {
                    port = 80;
                } else if ("wss".equalsIgnoreCase(scheme)) {
                    port = 443;
                } else {
                    port = -1;
                }
            } else {
                port = uri.getPort();
            }

            if (!"ws".equalsIgnoreCase(scheme) && !"wss".equalsIgnoreCase(scheme)) {
                log.error("仅支持WS(S)协议！！！");
                return;
            }

            EventLoopGroup group = new NioEventLoopGroup();
            WebSocketClientHandshaker handshaker = WebSocketClientHandshakerFactory.newHandshaker(uri, WebSocketVersion.V13, null, false, new DefaultHttpHeaders(), 65536 * 30);
            WebSocketClientHandler handler = new WebSocketClientHandler();
            handler.setHandshaker(handshaker);
            Bootstrap b = new Bootstrap();
            b.group(group).option(ChannelOption.TCP_NODELAY, true)
                    .channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) {
                    ChannelPipeline p = ch.pipeline();
                    p.addLast(new HttpClientCodec(), new HttpObjectAggregator(1024 * 1024 * 10), handler);
                }
            });

            try {

                Channel ch = b.connect(uri.getHost(), port).sync().channel();
                //阻塞等待是否握手成功
                handler.handshakeFuture().sync();
                ch.closeFuture().sync();

            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                group.shutdownGracefully();
                log.info("webSocketClient已经断开");
            }

        } catch (URISyntaxException e) {
            log.error("websocket服务器url不符合规范！", e);
        }

    }

    public static void main(String[] args) {
        String URL = System.getProperty("url", "ws://127.0.0.1:12550/ws");
        WebSocketClient webSocketClient = new WebSocketClient();
        webSocketClient.startClient(URL);
    }

}
