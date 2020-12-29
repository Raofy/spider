package com.jin10.spider.common.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author hoda.fang
 * @date 2019/12/2 15:14
 * ----------------------------------------------
 * 自定义参数配置
 * ----------------------------------------------
 */
@Configuration
@Data
public class CustomConfig {

    /**
     * 接收通知者
     */
    @Value("${custom.dingding.receiver}")
    public List<String> receiver;

    /**
     * 调度警告
     */
    @Value("${custom.dingding.adminUrl}")
    public String adminUrl;


    /**
     * 爬虫调度监控机器人地址
     */
    @Value("${custom.dingding.httpDispatchUrl}")
    public String httpDispatchUrl;

    /**
     * 爬虫机器人地址
     */
    @Value("${custom.dingding.secretKeyUrl}")
    public String secretKeyUrl;

    /**
     * 签名
     */
    @Value("${custom.dingding.secretKey}")
    public String secretKey;


    /**
     * ip 校验国内ping 地址
     */
    @Value("${custom.ping.mainUrl}")
    public String mainUrl;

    /**
     * Ip 校验国外地址
     */
    @Value("${custom.ping.foreignUrl}")
    public String foreignUrl;


    /**
     * 本机地址
     */
    @Value("${custom.nativeAddress}")
    public String nativeAddress;

    /**
     * 终端地址
     */
    @Value("${custom.serverAddress}")
    public String serverAddress;


}
