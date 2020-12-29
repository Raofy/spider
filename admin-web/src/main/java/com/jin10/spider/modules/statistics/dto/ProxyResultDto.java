package com.jin10.spider.modules.statistics.dto;

import lombok.Data;

/**
 * @author hongda.fang
 * @date 2019-12-12 15:34
 * ----------------------------------------------
 */
@Data
public class ProxyResultDto {

    private String proxyIp;

    /**
     * 爬取总任务量
     */
    private long all;
    /**
     *  成功数量
     */
    private long success;
    /**
     * 失败数量
     */
    private long fail;


}
