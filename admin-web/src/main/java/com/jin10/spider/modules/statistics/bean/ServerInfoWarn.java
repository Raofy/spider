package com.jin10.spider.modules.statistics.bean;

import lombok.Data;

/**
 * @author hongda.fang
 * @date 2019-12-13 11:54
 * ----------------------------------------------
 * 爬虫 服务器 警告
 */
@Data
public class ServerInfoWarn extends BaseWarnBean{

    public ServerInfoWarn(){

    }

    public ServerInfoWarn(int intervalTime1, int intervalTime2, String type, String ip) {
        super(intervalTime1, intervalTime2, type);
        this.ip = ip;
    }

    private String ip;

    private ServerInfo.CpuEntity cpu;

    private ServerInfo.MemEntity mem;

    private ServerInfo serverInfo;




}
