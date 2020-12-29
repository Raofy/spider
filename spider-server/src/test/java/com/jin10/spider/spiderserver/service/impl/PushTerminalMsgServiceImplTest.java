package com.jin10.spider.spiderserver.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Author: pancg
 * @Date: 2020/11/9 11:54
 */
@SpringBootTest
class PushTerminalMsgServiceImplTest {

    @Resource
    PushTerminalMsgServiceImpl pushTerminalMsgService;

    @Test
    void doAction() {
        String message = "{\"action\":370,\"ip\":\"127.0.0.1\"," +
                "\"msgIdList\":[\"2601306b8ca34b508ecf2b2b999b0411\"]}";
        pushTerminalMsgService.doAction(null,message);
    }
}