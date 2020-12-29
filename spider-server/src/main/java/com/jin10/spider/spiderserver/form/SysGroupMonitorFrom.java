package com.jin10.spider.spiderserver.form;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: pancg
 * @Date: 2020/8/28 11:40
 */
@Data
public class SysGroupMonitorFrom implements Serializable {

    /**
     * 来源
     */
    private String source;

    /**
     * 分类
     */
    private String category;

    /**
     * 消息频道
     */
    private String channel;
}
