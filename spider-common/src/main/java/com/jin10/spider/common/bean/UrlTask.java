package com.jin10.spider.common.bean;


import lombok.Data;

import java.util.Date;

@Data
public class UrlTask implements Comparable<UrlTask> {


    /**
     * 模版 ID
     */
    private Long tempId;

    /**
     * 爬虫地址
     */
    private String url;

    /**
     * 是否国外
     */
    private boolean weForeign;

    /**
     * 任务唯一ID
     */
    private String taskUuid;


    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 发送给websocket服务器的时间
     */
    private Date sendS1Time;

    /**
     * websocket服务器接收到的时间
     */
    private Date receiveS1Time;

    /**
     * 推送给爬虫的时间
     */
    private Date pushTime;



    private Integer priority;

    /**
     * 是否测试的任务
     */
    private boolean isTest;

    private boolean puded = true;

    private Object temp;


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof UrlTask && tempId != null) {
            UrlTask task = (UrlTask) obj;
            return tempId.equals(task.getTempId());
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        if (tempId != null) {
            return tempId.hashCode();
        }
        return super.hashCode();
    }

    @Override
    public int compareTo(UrlTask o) {
        if (priority != null && o.getPriority() != null) {
            return -priority.compareTo(o.getPriority());
        }
        return 0;
    }


}
