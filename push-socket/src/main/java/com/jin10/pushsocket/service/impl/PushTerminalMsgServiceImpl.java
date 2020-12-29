package com.jin10.pushsocket.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.jin10.pushsocket.annotation.ActionCode;
import com.jin10.pushsocket.annotation.SocketResponseBody;
import com.jin10.pushsocket.constants.ActionCodeConstants;
import com.jin10.pushsocket.constants.DataCache;
import com.jin10.pushsocket.entity.SpiderMessageElastics;
import com.jin10.pushsocket.entity.SpiderMessagePush;
import com.jin10.pushsocket.entity.SpiderMessageVO;
import com.jin10.pushsocket.enums.MsgCodeEnum;
import com.jin10.pushsocket.interf.IActionSocketService;
import com.jin10.pushsocket.service.ISpiderMessagePushService;
import com.jin10.pushsocket.service.ISpiderMsgElasticSearchService;
import com.jin10.pushsocket.utils.NettyUtils;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


import java.util.ArrayList;
import java.util.List;

/**
 * @author Airey
 * @date 2020/3/5 9:27
 * ----------------------------------------------
 * 推送消息
 * ----------------------------------------------
 */
@Service
@ActionCode(value = ActionCodeConstants.PUSH_MSG)
@Slf4j
public class PushTerminalMsgServiceImpl implements IActionSocketService {


    @Autowired
    private ISpiderMessagePushService pushService;
    @Autowired
    private ISpiderMsgElasticSearchService searchService;
    @Autowired
    private NettyUtils nettyUtils;


    @Override
    @SocketResponseBody
    public Object doAction(ChannelHandlerContext context, String message) {
        log.info("协议号" + ActionCodeConstants.PUSH_MSG + "，开始处理业务请求消息 : " + message);
        JSONObject requestJson = JSONObject.parseObject(message);
        return handlePushMsg(requestJson);
    }

    /**
     * 处理推送消息
     *
     * @param requestJson
     */
    public JSONObject handlePushMsg(JSONObject requestJson) {

        long startTime = System.currentTimeMillis();
        String env = requestJson.getString("env");
        List<String> msgIdList = (List<String>) requestJson.get("msgIdList");
        if (CollUtil.isEmpty(msgIdList)) {
            log.error("推送的消息列表为NULL！！！");
            return getResponseMsg(false, msgIdList);
        }
        List<SpiderMessageElastics> resultList = searchService.findByMsgIdIn(msgIdList);
        List<SpiderMessageVO> messageVOList = new ArrayList<>();
        for (SpiderMessageElastics item : resultList) {
            SpiderMessagePush messagePush = new SpiderMessagePush();
            BeanUtils.copyProperties(item, messagePush);
            messagePush.setInsertTime(null);
            if (StringUtils.isNotBlank(env)) {
                messagePush.setEnv(env);
            }
            try {
                boolean save = pushService.save(messagePush);
                if (save) {
                    log.info("数据推送成功！ msgId = " + messagePush.getMsgId() + " 入库成功！");
                }
            } catch (DuplicateKeyException e) {
                log.error("数据库已经存在 msgId = " + messagePush.getMsgId() + " 的消息记录！！！");
                continue;
            }
            SpiderMessageVO messageVO = new SpiderMessageVO();
            BeanUtils.copyProperties(item, messageVO);
            if (DataCache.categoryMap.containsKey(item.getCategory())) {
                messageVO.setCategoryColor((String) DataCache.categoryMap.get(item.getCategory()));
            }
            messageVOList.add(messageVO);
        }
        if (CollectionUtils.isEmpty(messageVOList)) {
            return getResponseMsg(false, msgIdList);
        }

        JSONObject resultJson = new JSONObject();
        resultJson.put("list", messageVOList);
        resultJson.put("label", DataCache.labelMap);
        if (StringUtils.isNotBlank(env)) {
            resultJson.put("env", env);
        } else {
            resultJson.put("env", "prod");
        }
        nettyUtils.pushRegisterActionChannel(ActionCodeConstants.PUSH_MSG_GROUP, MsgCodeEnum.PUSH, resultJson, true);
        long endTime = System.currentTimeMillis();
        log.info("推送消息完毕 ！！！ ==> " + msgIdList + " , 耗时 = " + (endTime - startTime) + " /ms");
        return getResponseMsg(true, msgIdList);
    }


    /**
     * 组装返回的json
     *
     * @param flag
     * @param msgIdList
     * @return
     */
    private JSONObject getResponseMsg(boolean flag, List<String> msgIdList) {

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("msgType", 373);
        if (flag) {
            jsonObj.put("code", 200);
            jsonObj.put("msg", "推送消息成功！！！");
        } else {
            jsonObj.put("code", 500);
            jsonObj.put("msg", "推送消息失败！！！");
        }
        jsonObj.put("data", msgIdList);
        return jsonObj;
    }


}
