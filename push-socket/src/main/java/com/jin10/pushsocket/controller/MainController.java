package com.jin10.pushsocket.controller;

import com.alibaba.fastjson.JSONObject;
import com.jin10.pushsocket.bean.BaseResponse;
import com.jin10.pushsocket.service.impl.PushTerminalMsgServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Airey
 * @date 2020/5/29 15:09
 * ----------------------------------------------
 * TODO
 * ----------------------------------------------
 */
@RestController
@RequestMapping("main")
@Slf4j
public class MainController {

    @Autowired
    private PushTerminalMsgServiceImpl pushTerminalMsgService;


    @PostMapping("pushMsg")
    public BaseResponse pushMsg(@RequestBody JSONObject jsonObject) {
        log.info("接收到需推送的消息 " + jsonObject);
        pushTerminalMsgService.handlePushMsg(jsonObject);
        return BaseResponse.ok();
    }


}
