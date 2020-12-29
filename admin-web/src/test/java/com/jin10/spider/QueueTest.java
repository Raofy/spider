package com.jin10.spider;

import com.jin10.spider.common.bean.UrlTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * @author hongda.fang
 * @date 2019-11-06 12:01
 * ----------------------------------------------
 */
public class QueueTest {

    private static PriorityBlockingQueue<UrlTask> queue = new PriorityBlockingQueue<>();

    public static void main(String[] args) {


        long l = System.currentTimeMillis();

        for (int i = 0 ; i < 10 ; i++){
            UrlTask urlTasks = new UrlTask();
            urlTasks.setPriority(i);
            urlTasks.setTempId((long) i);
            queue.add(urlTasks);
            boolean contains = queue.contains(urlTasks);
            String a = "";

        }
        UrlTask urlTask = new UrlTask();
        urlTask.setTempId(2L);
//        boolean remove = queue.remove(urlTask);

        queue.size();

        List<UrlTask>  list = new ArrayList<>(queue);

        UrlTask peek = queue.peek();
        UrlTask peek1 = queue.peek();
        UrlTask peek2 = queue.peek();

        String b = "";
//        long l1 = System.currentTimeMillis();
//
//        for (int i = 0 ; i < 100000 ; i++){
//            queue.poll();
//        }
//
//        long l2 = System.currentTimeMillis();
//
//
//        System.out.println(l1 -l);
//
//        System.out.println(l2 -l1);
//        System.out.println(l2 -l);
        UrlTask urlTasks = new UrlTask();
        urlTasks.setPriority(1);
        urlTasks.setTempId(9999999L);


        boolean contains = queue.contains(urlTasks);
        String a = "";

    }

}
