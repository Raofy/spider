package com.jin10.pushsocket.mainThread;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketFrameAggregator;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.UnorderedThreadPoolEventExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NioWebSocketChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Autowired
    private NioWebSocketHandler socketHandler;


//    private MetricHandler metricHandler = new MetricHandler();

    UnorderedThreadPoolEventExecutor businessGroup = new UnorderedThreadPoolEventExecutor(10, new DefaultThreadFactory("business"));

    @Override
    protected void initChannel(SocketChannel ch) {
        //设置log监听器，并且日志级别为debug，方便观察运行流程
        ch.pipeline().addLast("logging", new LoggingHandler(LogLevel.DEBUG));
        //设置解码器
        ch.pipeline().addLast("http-codec", new HttpServerCodec());
        //聚合器，使用websocket会用到
        ch.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));
        ch.pipeline().addLast("websocket-aggregator", new WebSocketFrameAggregator(65536 * 30));
        //用于大数据的分区传输
        ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());

//        ch.pipeline().addLast("metricsHandler", metricHandler);
        //自定义的业务handler
        ch.pipeline().addLast(businessGroup,"handler", socketHandler);
        //默认的group处理
//        ch.pipeline().addLast("handler", socketHandler);



    }
}
