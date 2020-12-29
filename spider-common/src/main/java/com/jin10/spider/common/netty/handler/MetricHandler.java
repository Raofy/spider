package com.jin10.spider.common.netty.handler;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jmx.JmxReporter;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Airey
 * @date 2020/5/14 17:51
 * ----------------------------------------------
 * Netty应用监控
 * ----------------------------------------------
 */

@ChannelHandler.Sharable
public class MetricHandler extends ChannelDuplexHandler {

    private AtomicLong totalConnectionNumber = new AtomicLong();

    {
        MetricRegistry metricRegistry = new MetricRegistry();
        metricRegistry.register("totalConnectionNumber", (Gauge<Long>) () -> totalConnectionNumber.longValue());

        ConsoleReporter consoleReporter = ConsoleReporter.forRegistry(metricRegistry).build();
        consoleReporter.start(60, TimeUnit.SECONDS);

        JmxReporter jmxReporter = JmxReporter.forRegistry(metricRegistry).build();
        jmxReporter.start();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        totalConnectionNumber.incrementAndGet();
        super.channelActive(ctx);
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        totalConnectionNumber.decrementAndGet();
        super.channelInactive(ctx);
    }
}
