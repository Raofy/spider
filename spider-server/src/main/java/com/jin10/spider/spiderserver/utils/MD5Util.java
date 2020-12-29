package com.jin10.spider.spiderserver.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;

/**
 * @author Airey
 * @date 2020/5/28 15:50
 * ----------------------------------------------
 * md5工具类
 * ----------------------------------------------
 */
public class MD5Util {

    /**
     * 监控消息类别
     *
     * @param source
     * @param category
     * @param channel
     * @return
     */
    public static String getMessageMonitorCode(String source, String category, String channel) {
        String temp = "{}:{}:{}";
        String key = StrUtil.format(temp, source, category, channel);
        return SecureUtil.md5(key);
    }


    public static void main(String[] args) {
        String messageMonitorCode = getMessageMonitorCode("发改委", "文章", "通知");
        System.out.println(messageMonitorCode);
        String md5 = SecureUtil.md5("新浪微博:微博:第三方推送快讯");
        System.out.println(md5);
    }

}
