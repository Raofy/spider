package com.jin10.spider.common.netty.global;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Airey
 * @date 2020/5/19 12:42
 * ----------------------------------------------
 * 全局消息数量计数器
 * ----------------------------------------------
 */
public class GlobalCounter {


    public static AtomicLong socket_global_msg_total_count = new AtomicLong(0L);

    public static AtomicLong term_global_msg_total_count = new AtomicLong(0L);

    public static AtomicLong socket_global_msg_fullhttp_count = new AtomicLong(0L);

    public static AtomicLong term_global_msg_fullhttp_count = new AtomicLong(0L);

    public static AtomicLong socket_global_msg_bus_count = new AtomicLong(0L);

    public static AtomicLong term_global_msg_bus_count = new AtomicLong(0L);


    public static void main(String[] args) {
        long l = GlobalCounter.socket_global_msg_fullhttp_count.get();
        System.out.println("l = " + l);
        GlobalCounter.socket_global_msg_bus_count.set(6L);
        long l1 = GlobalCounter.socket_global_msg_bus_count.get();
        System.out.println("l1= " + l1);
    }

}