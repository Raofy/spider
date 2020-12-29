package com.jin10.spider.spiderserver.constants;


import java.util.regex.Pattern;

/**
 * @author Airey
 * @date 2019/11/18 17:55
 * ----------------------------------------------
 * 全局常量类
 * ----------------------------------------------
 */
public interface GlobalConstants {


    /**
     * 默认搜索的字体颜色
     */
    String SEARCH_COLOR = "#FF1493";


    /**
     * 默认存储100条消息
     */
    int CACHE_QUEUE_SIZE = 200;


    /**
     * 钉钉返回内容中包含的前缀
     */
    String DINGDING_PREFIX = "类别不匹配";

    /**
     * 爬虫调度返回前缀
     */
    String DISPATCH_PREFIX = "监控源变更";


    /**
     * 数据中心类别
     */
    String DATA_CENTER = "数据中心";

    /**
     * 中国人民银行
     */
    String PEOPLE_BANK_OF_CHINA = "中国人民银行";

    /**
     * 匹配正则表达式的网址
     */
    Pattern HTTP_PATTERN = Pattern.compile("[a-zA-z]+://[^\\s]*");

    String HTTP_TEMP = "<a href=\"{}\" target=\"_blank\">{}</a>";

    String GROUP_NAME = "默认分组";
}
