package com.jin10.spider.common.netty.handler;

import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.HashBiMap;
import com.jin10.spider.common.constants.ActionCodeConstants;
import com.jin10.spider.common.constants.RedisKey;
import com.jin10.spider.common.netty.global.ChannelSupervise;
import com.jin10.spider.common.netty.global.GlobalCounter;
import com.jin10.spider.common.netty.service.TcpDispacher;
import com.jin10.spider.common.utils.RedisUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static io.netty.handler.codec.http.HttpUtil.isKeepAlive;
import static io.netty.handler.codec.http.HttpHeaderNames.HOST;


@Component
@ChannelHandler.Sharable
@Slf4j
public class NioWebSocketHandler extends SimpleChannelInboundHandler<Object> {


    private WebSocketServerHandshaker handshaker;

    @Autowired
    private TcpDispacher tcpDispacher;

    @Autowired
    private RedisUtils redisUtils;


    // websocket 服务的 uri
    private static final String WEBSOCKET_PATH = "/websocket";


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

        GlobalCounter.socket_global_msg_total_count.incrementAndGet();
        GlobalCounter.term_global_msg_total_count.incrementAndGet();

        if (msg instanceof FullHttpRequest) {
            //以http请求形式接入，但是走的是websocket
            log.info("收到消息：" + msg);
            GlobalCounter.socket_global_msg_fullhttp_count.incrementAndGet();
            GlobalCounter.term_global_msg_fullhttp_count.incrementAndGet();
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            GlobalCounter.socket_global_msg_bus_count.incrementAndGet();
            GlobalCounter.term_global_msg_bus_count.incrementAndGet();
            //处理websocket客户端的消息
            handlerWebSocketFrame(ctx, (WebSocketFrame) msg);
        } else {
            log.info("未知消息 : " + msg);
        }


    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //添加连接
        log.info("客户端加入连接：" + ctx.channel());
        ChannelSupervise.addChannel(ctx.channel());
//        SocketActiveRequest socketActiveRequest = new SocketActiveRequest();
//        socketActiveRequest.setIp(NettyUtils.getClientIP(ctx));
//        socketActiveRequest.setStatus(Constants.SOCKET_STATUS.LOGIN);
//        tcpDispacher.messageRecived(ctx, JsonUtil.toJson(socketActiveRequest));
    }


    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        log.info("收到" + incoming.remoteAddress() + " 握手请求");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //断开连接
        log.info("客户端断开连接：" + ctx.channel());
        removeSpiderZset(ctx.channel());
        ChannelSupervise.removeChannel(ctx.channel());

//        SocketActiveRequest socketActiveRequest = new SocketActiveRequest();
//        socketActiveRequest.setIp(NettyUtils.getClientIP(ctx));
//        socketActiveRequest.setStatus(Constants.SOCKET_STATUS.LOGOUT);
//        tcpDispacher.messageRecived(ctx, JsonUtil.toJson(socketActiveRequest));

    }

    /**
     * 移除zset的队列
     *
     * @param channel
     */
    private void removeSpiderZset(Channel channel) {

        if (CollUtil.isEmpty(ChannelSupervise.actionIpGlobalGroupMap)) {
            return;
        }
        for (Integer actionCode : ChannelSupervise.actionIpGlobalGroupMap.keySet()) {
            if (actionCode == ActionCodeConstants.TASK_PUSH) {
                Map<String, Channel> ipChannelMap = ChannelSupervise.actionIpGlobalGroupMap.get(actionCode);
                if (!CollUtil.isEmpty(ipChannelMap)) {
                    HashBiMap<String, Object> biMap = HashBiMap.create();
                    biMap.putAll(ipChannelMap);
                    if (ipChannelMap.containsValue(channel)) {
                        redisUtils.zRemove(RedisKey.SPIDER_NODE_ZSET, biMap.inverse().get(channel));
                    }
                }
            }
            if (actionCode == ActionCodeConstants.TASK_PUSH_FOREIGN) {
                Map<String, Channel> ipChannelMap = ChannelSupervise.actionIpGlobalGroupMap.get(actionCode);
                if (!CollUtil.isEmpty(ipChannelMap)) {
                    HashBiMap<String, Object> biMap = HashBiMap.create();
                    biMap.putAll(ipChannelMap);
                    if (ipChannelMap.containsValue(channel)) {
                        redisUtils.zRemove(RedisKey.SPIDER_NODE_FOREIGN_ZSET, biMap.inverse().get(channel));
                    }
                }
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        log.error("netty 异常处理！！！", cause);
        log.error("netty 异常原因 ===》{} ", cause.getMessage());
        Channel channel = ctx.channel();
        if (channel.isActive()) {
            ctx.close();
        }
    }

    private void handlerWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        /**
         * 判断是否关闭链路的指令
         */
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        /**
         * 判断是否ping消息
         */
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        /**
         * 仅支持文本消息，不支持二进制消息
         */
        if (!(frame instanceof TextWebSocketFrame)) {
            log.debug("仅支持文本消息，不支持二进制消息");
            throw new UnsupportedOperationException(String.format(
                    "%s frame types not supported", frame.getClass().getName()));
        }


        /**
         * 返回应答消息
         */
        String request = ((TextWebSocketFrame) frame).text();

        tcpDispacher.messageRecived(ctx, request);


//
//        logger.debug("服务端收到：" + request);
//        TextWebSocketFrame tws = new TextWebSocketFrame(new Date().toString()
//                + ctx.channel().id() + "：" + request);
//        /**
//         * 群发
//         * ChannelSupervise.send2All(tws);
//         */
//
//        /**
//         * 返回【谁发的发给谁】
//         */
//         ctx.channel().writeAndFlush(tws);
    }

    /**
     * 唯一的一次http请求，用于创建websocket
     */
    private void handleHttpRequest(ChannelHandlerContext ctx,
                                   FullHttpRequest req) {
        //要求Upgrade为websocket，过滤掉get/Post
        if (!req.decoderResult().isSuccess()
                || (!"websocket".equals(req.headers().get("Upgrade")))) {
            //若不是websocket方式，则创建BAD_REQUEST的req，返回给客户端
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }

        String webSocketUrl = getWebSocketLocation(req);
        log.info("url : " + webSocketUrl);
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(webSocketUrl, null, false, 65536 * 20);
        handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory
                    .sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), req);
        }
    }

    /**
     * 拒绝不合法的请求，并返回错误信息
     */
    private static void sendHttpResponse(ChannelHandlerContext ctx,
                                         FullHttpRequest req, DefaultFullHttpResponse res) {
        /**
         *  返回应答给客户端
         */
        if (res.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(),
                    CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
        }
        ChannelFuture f = ctx.channel().writeAndFlush(res);

        /**
         * 如果是非Keep-Alive，关闭连接
         */
        if (!isKeepAlive(req) || res.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }


    private static String getWebSocketLocation(FullHttpRequest req) {
        String location = req.headers().get(HOST) + WEBSOCKET_PATH;
        return "ws://" + location;
    }
}
