package com.jin10.spider.spiderserver.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author Airey
 * @date 2019/12/2 15:14
 * ----------------------------------------------
 * TODO
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
     * 消息类别监控钉钉机器人地址
     */
    @Value("${custom.dingding.httpUrl}")
    public String httpUrl;


    /**
     * 爬虫调度监控机器人地址
     */
    @Value("${custom.dingding.httpDispatchUrl}")
    public String httpDispatchUrl;

    /**
     * 爬虫调度端地址
     */
    @Value("${custom.admin.url}")
    public String adminUrl;

    /**
     * 金十网关check字符串
     */
    @Value("${custom.check}")
    public String check;

    /**
     * 推送http地址
     */
    @Value("${custom.push.url}")
    public String pushUrl;

}
