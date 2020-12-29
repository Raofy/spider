package com.jin10.spider.common.bean;

import lombok.Data;

import java.nio.channels.Channel;

/**
 * @author Airey
 * @date 2020/1/17 18:19
 * ----------------------------------------------
 * 调度实体类:调度不同爬虫节点均匀分配执行
 * ----------------------------------------------
 */
@Data
public class DispatchChannel {

    /**
     * ip
     */
    private String ip;

    /**
     * socket通道
     */
    private Channel channel;

    /**
     * 推送次数
     */
    private long pushTimes;


}
