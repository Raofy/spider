package com.jin10.spider.spiderserver.constants;

import com.jin10.spider.spiderserver.vo.SpiderMessageVO;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import static com.jin10.spider.spiderserver.constants.GlobalConstants.CACHE_QUEUE_SIZE;

/**
 * @author Airey
 * @date 2019/11/27 15:52
 * ----------------------------------------------
 * 数据缓存类
 * ----------------------------------------------
 */
public class DataCache {


    /**
     * 缓存最新的标签库 ConcurrentHashMap不允许空值和空键
     */
    public static Map<String, Object> labelMap = new ConcurrentHashMap<>();

    /**
     * 缓存最新的消息类别库
     */
    public static Map<String, Object> categoryMap = new ConcurrentHashMap<>();

    /**
     * 缓存最新的爬虫消息  size=GlobalConstants.CACHE_QUEUE_SIZE
     */
    public static LinkedBlockingQueue<SpiderMessageVO> SpiderMessageQueue = new LinkedBlockingQueue<>(CACHE_QUEUE_SIZE);

    /**
     * 缓存需要自动推送的消息类别
     */
    public static Map<String, String> autoPushMap=new ConcurrentHashMap<>();
}
