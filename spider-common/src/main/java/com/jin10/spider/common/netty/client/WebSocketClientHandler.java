package com.jin10.spider.common.netty.client;


import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.jin10.spider.common.netty.global.ClientChannelGroup.clientGroup;

/**
 * @author Airey
 * @date 2020/1/16 14:49
 * ----------------------------------------------
 * 客户端websockt处理类
 * ----------------------------------------------
 */
@Slf4j
@Service
public class WebSocketClientHandler extends SimpleChannelInboundHandler<Object> {

    @Autowired
    private WebSocketClient webSocketClient;

    private WebSocketClientHandshaker handshaker;
    private ChannelPromise handshakeFuture;


    public WebSocketClientHandler() {

    }

    public WebSocketClientHandshaker getHandshaker() {
        return handshaker;
    }

    public void setHandshaker(WebSocketClientHandshaker handshaker) {
        this.handshaker = handshaker;
    }

    public ChannelFuture handshakeFuture() {
        return handshakeFuture;
    }


    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        log.info("handlerAdded");
        handshakeFuture = ctx.newPromise();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("channelActive");
        clientGroup.add(ctx.channel());
        log.info("ctx : " + ctx);
        handshaker.handshake(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("WebSocket Client 链接失败!");
        clientGroup.remove(ctx.channel());

//        //使用过程中断线重连
//        final EventLoop eventLoop = ctx.channel().eventLoop();
//        eventLoop.schedule(() -> {
//            log.info("开始断线重连！！！！");
//            webSocketClient.startClient("ws://127.0.0.1:12550/ws");
//
//        }, 1, TimeUnit.SECONDS);

    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
//        log.info("channelRead0");
        Channel ch = ctx.channel();
        if (!handshaker.isHandshakeComplete()) {
            try {
                handshaker.finishHandshake(ch, (FullHttpResponse) msg);
                log.info("WebSocket Client connected!");
                handshakeFuture.setSuccess();
            } catch (WebSocketHandshakeException e) {
                log.info("WebSocket Client failed to connect");
                handshakeFuture.setFailure(e);

            }
            return;
        }

        if (msg instanceof FullHttpResponse) {
            FullHttpResponse response = (FullHttpResponse) msg;
            throw new IllegalStateException("Unexpected FullHttpResponse (getStatus=" + response.getStatus()
                    + ", content=" + response.content().toString(CharsetUtil.UTF_8) + ')');
        }

        WebSocketFrame frame = (WebSocketFrame) msg;
        if (frame instanceof TextWebSocketFrame) {
            TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
//            ch.writeAndFlush(textFrame.text());
//            log.info("WebSocket Client received message: " + textFrame.text());
        } else if (frame instanceof BinaryWebSocketFrame) {
            BinaryWebSocketFrame binFrame = (BinaryWebSocketFrame) frame;
            log.info("BinaryWebSocketFrame");
        } else if (frame instanceof PongWebSocketFrame) {
            log.info("WebSocket Client received pong");
        } else if (frame instanceof CloseWebSocketFrame) {
            log.info("WebSocket Client received closing");
            ch.close();
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        log.error("Netty Client 处理异常 ！！！", cause);
        if (!handshakeFuture.isDone()) {
            handshakeFuture.setFailure(cause);
        }
        ctx.close();
    }

}
