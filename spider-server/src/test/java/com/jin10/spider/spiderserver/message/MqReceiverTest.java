package com.jin10.spider.spiderserver.message;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Author: pancg
 * @Date: 2020/10/16 15:56
 */
@SpringBootTest
class MqReceiverTest {

    @Autowired
    private MqReceiver mqReceiver;

    @Test
    void dealMsg() {
        String message = "{\"data\":[{\"title\":\"<span class=\\\"new\\\"></span>【快讯】20201016递延方向及结算价\\nAg(T D)  多付空 5058\\nAu(T D)   空付多403.03\\nmAu(T D) 多付空 403.18\",\"url\":\"\"}],\"channel\":\"第三方推送快讯\",\"source\":\"口袋贵金属\",\"time\":1602833653000,\"title\":\"<span class=\\\"new\\\"></span>【快讯】20201016递延方向及结算价\\nAg(T D)  多付空 5058\\nAu(T D)   空付多403.03\\nmAu(T D) 多付空 403.18\",\"category\":\"app推送\",\"url\":\"\",\"taskId\":\"30127985871845c09479382b114943aa\"}";
        mqReceiver.dealMsg(message);
    }
}