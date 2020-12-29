package com.jin10.spider.spiderserver;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Airey
 * @date 2019/11/22 18:00
 * ----------------------------------------------
 * TODO
 * ----------------------------------------------
 */
public class LinkedBlockingQueueTest {

    public static void main(String[] args) {
        LinkedBlockingQueue queue = new LinkedBlockingQueue(3);
        queue.add("张三");
        queue.add("李四");
        queue.add("王五");
        System.out.println(queue.size());
        System.out.println(queue);


    }

}
