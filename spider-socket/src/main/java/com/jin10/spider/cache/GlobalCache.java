package com.jin10.spider.cache;

import com.jin10.spider.bean.IpDispatch;
import com.jin10.spider.bean.UrlTaskDtoPri;

import java.util.concurrent.PriorityBlockingQueue;

/**
 * @author Airey
 * @date 2020/2/3 16:54
 * ----------------------------------------------
 * 全局缓存对象
 * ----------------------------------------------
 */
public class GlobalCache {


    /**
     * ip优先级调度列表
     */
    public static PriorityBlockingQueue<IpDispatch> ipDispatchQueue = new PriorityBlockingQueue<>();


    /**
     * ip优先级调度列表 国外
     */
    public static PriorityBlockingQueue<IpDispatch> ipForeignDispatchQueue = new PriorityBlockingQueue<>();


    /**
     * 缓存任务优先级列表（任务队列）
     */
    public static PriorityBlockingQueue<UrlTaskDtoPri> taskDtoPrisQueue = new PriorityBlockingQueue<>();

    /**
     * 缓存任务优先级列表 国外
     */
    public static PriorityBlockingQueue<UrlTaskDtoPri> taskDtoPrisForeignQueue = new PriorityBlockingQueue<>();


}
