package com.jin10.pushsocket.constants;

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


    public static AtomicLong socket_global_msg_fullhttp_count = new AtomicLong(0L);


    public static AtomicLong socket_global_msg_bus_count = new AtomicLong(0L);


}