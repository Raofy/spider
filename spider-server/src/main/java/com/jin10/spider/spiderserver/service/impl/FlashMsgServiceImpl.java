package com.jin10.spider.spiderserver.service.impl;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jin10.spider.common.annotation.ActionCode;
import com.jin10.spider.common.constants.ActionCodeConstants;
import com.jin10.spider.common.netty.interf.IActionSocketService;
import com.jin10.spider.spiderserver.message.MqReceiver;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @author Airey
 * @date 2020/2/17 19:26
 * ----------------------------------------------
 * 接收快讯消息并处理
 * ----------------------------------------------
 */
@Service
@ActionCode(value = ActionCodeConstants.FLASH_MSG)
@Slf4j
public class FlashMsgServiceImpl implements IActionSocketService {

    @Autowired
    private MqReceiver mqReceiver;


    @Override
    public Object doAction(ChannelHandlerContext context, String message) {


        log.info("接收到客户端传输的快讯消息: " + message);
        JSONObject jsonObject = JSONObject.parseObject(message);
        JSONObject data = jsonObject.getJSONObject("data");
        handleMsg(data);
        mqReceiver.dealMsg(data.toJSONString());

        return null;
    }


    /**
     * 模块化消息
     *
     * @param jsonObject
     */
    private void handleMsg(JSONObject jsonObject) {


        jsonObject.put("taskId", IdUtil.fastSimpleUUID());
        JSONArray jsonArray = new JSONArray();
        JSONObject resultJ = new JSONObject();
        resultJ.put("url", jsonObject.getString("url"));
        resultJ.put("title", jsonObject.getString("title"));
        jsonArray.add(resultJ);
        jsonObject.put("data", jsonArray);

    }


}
