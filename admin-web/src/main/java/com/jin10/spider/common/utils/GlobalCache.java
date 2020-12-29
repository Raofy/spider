package com.jin10.spider.common.utils;

import com.jin10.spider.common.bean.UrlTask;
import com.jin10.spider.modules.template.entity.Template;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * @author hongda.fang
 * @date 2019-12-02 11:21
 * ----------------------------------------------
 */
public class GlobalCache {
    /**
     * 需要爬的队列(国内)
     */
    private static PriorityBlockingQueue<UrlTask> productQueue = new PriorityBlockingQueue<>();
    /**
     * 需要爬的队列(国外)
     */
    private static PriorityBlockingQueue<UrlTask> productQueueForeign = new PriorityBlockingQueue<>();

    /**
     * 已经生产的任务
     */
    private static ConcurrentHashMap<String, UrlTask> productTaskMap = new ConcurrentHashMap();
    /**
     * 记录任务被领取的时间
     */
    private static ConcurrentHashMap<String, Date> productGetedDateTaskMap = new ConcurrentHashMap();

    public static Map<String, Template> templateMap=new HashMap<>();

    public static Map<String, String> listTempMap = new HashMap<>();


    public static PriorityBlockingQueue<UrlTask> getProductQueue() {
        return productQueue;
    }

    public static PriorityBlockingQueue<UrlTask> getProductQueueForeign() {
        return productQueueForeign;
    }

    public static ConcurrentHashMap<String, UrlTask> getProductTaskMap() {
        return productTaskMap;
    }

    public static ConcurrentHashMap<String, Date> getProductGetedDateTaskMap() {
        return productGetedDateTaskMap;
    }
}
