package com.jin10.pushsocket.entity;


import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Airey
 * @date 2019/11/28 10:40
 * ----------------------------------------------
 * SpiderMessage视图层对象
 * ----------------------------------------------
 */
@Data
public class SpiderMessageVO implements Serializable, Comparable<SpiderMessageVO> {


    private static final long serialVersionUID = 2326707030298992495L;


    /**
     * 唯一消息id
     */
    private String msgId;


    /**
     * 爬虫内容标题
     */
    private String title;

    /**
     * 爬虫内容对应的url
     */
    private String url;

    /**
     * 消息来源，或者网站名称
     */
    private String source;

    /**
     * 消息类型 1.文章 2.快讯
     */
    private String category;

    /**
     * 同一个标题可能有多个不同的频道
     */
    private String channel;


    /**
     * 爬虫爬取的日期
     */
    private Date time;

    /**
     * 消息类型对应的颜色
     */
    private String categoryColor;


    /**
     * 该消息是否已经被推送
     */
    private boolean pushFlag;


    /**
     * 环境变量
     */
    private String env;

    /**
     * 排序比较器
     *
     * @param o
     * @return
     */
    @Override
    public int compareTo(SpiderMessageVO o) {
        return o.getTime().compareTo(this.getTime());
    }
}
