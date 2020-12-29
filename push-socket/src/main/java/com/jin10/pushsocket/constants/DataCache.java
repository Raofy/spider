package com.jin10.pushsocket.constants;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


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


}
