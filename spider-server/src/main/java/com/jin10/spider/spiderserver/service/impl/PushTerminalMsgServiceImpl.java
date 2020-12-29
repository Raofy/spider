package com.jin10.spider.spiderserver.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jin10.spider.common.annotation.ActionCode;
import com.jin10.spider.common.annotation.SocketResponseBody;
import com.jin10.spider.common.constants.ActionCodeConstants;
import com.jin10.spider.common.enums.MsgCodeEnum;
import com.jin10.spider.common.netty.interf.IActionSocketService;
import com.jin10.spider.common.utils.NettyUtils;
import com.jin10.spider.spiderserver.constants.DataCache;
import com.jin10.spider.spiderserver.entity.SpiderMessageElastics;
import com.jin10.spider.spiderserver.entity.SpiderMessagePush;
import com.jin10.spider.spiderserver.service.ISpiderMessagePushService;
import com.jin10.spider.spiderserver.service.ISpiderMsgElasticSearchService;
import com.jin10.spider.spiderserver.vo.SpiderMessageVO;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional(rollbackFor = Exception.class)
    @SocketResponseBody
    @Override
    public Object doAction(ChannelHandlerContext context, String message) {

        log.info("开始处理业务请求消息 : " + message);
        List<SpiderMessageVO> messageList;
        long startTime = System.currentTimeMillis();
        JSONObject requestJson = JSONObject.parseObject(message);
        String env = requestJson.getString("env");
        JSONArray jsonArray = requestJson.getJSONArray("messageList");
        messageList = JSONObject.parseArray(jsonArray.toJSONString(),SpiderMessageVO.class);
        if (CollUtil.isEmpty(messageList)) {
            log.error("推送的消息列表为NULL！！！");
            return getResponseMsg(false, messageList);
        }
        List<SpiderMessageVO> messageVOList = new ArrayList<>();
        for (SpiderMessageVO item : messageList) {
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

            messageVOList.add(item);
        }
        if (CollectionUtils.isEmpty(messageVOList)) {
            return getResponseMsg(false, messageVOList);
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
        log.info("推送消息完毕 ！！！ ==> " + messageVOList + " , 耗时 = " + (endTime - startTime) + " /ms");
        return getResponseMsg(true, messageVOList);
    }


    /**
     * 组装返回的json
     *
     * @param flag
     * @param msgIdList
     * @return
     */
    private JSONObject getResponseMsg(boolean flag, List<SpiderMessageVO> msgIdList) {

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
