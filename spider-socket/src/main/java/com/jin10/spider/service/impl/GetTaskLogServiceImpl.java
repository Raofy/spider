package com.jin10.spider.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jin10.spider.common.annotation.ActionCode;
import com.jin10.spider.common.bean.MsgResponse;
import com.jin10.spider.common.constants.ActionCodeConstants;
import com.jin10.spider.common.enums.MsgCodeEnum;
import com.jin10.spider.common.netty.global.ChannelSupervise;
import com.jin10.spider.common.netty.interf.IActionSocketService;
import com.jin10.spider.utils.NettyChannelUtils;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Airey
 * @date 2020/2/16 19:44
 * ----------------------------------------------
 * 推送爬虫节点返回的日志信息
 * ----------------------------------------------
 */
@Service
@ActionCode(value = ActionCodeConstants.TASK_LOG_MSG)
@Slf4j
public class GetTaskLogServiceImpl implements IActionSocketService {

    @Autowired
    private NettyChannelUtils nettyChannelUtils;


    @Override
    public Object doAction(ChannelHandlerContext context, String message) {

        JSONObject jsonObject = JSONObject.parseObject(message);

        if (jsonObject.containsKey("taskLog")) {
            String taskLog = jsonObject.getString("taskLog");
            JSONObject taskLogObj = JSONObject.parseObject(taskLog);
            Integer tempId = taskLogObj.getInteger("tempId");
            String ip = taskLogObj.getString("ip");
            String proxyComplete = taskLogObj.getString("proxyComplete");

            MsgResponse msgResponse = new MsgResponse(MsgCodeEnum.TASK_LOG_RETURN, taskLogObj);
            nettyChannelUtils.pushRegisterByTempId(ActionCodeConstants.TASK_LOG_MSG, tempId, JSONObject.toJSONString(msgResponse));
            nettyChannelUtils.pushRegisterByIp(ActionCodeConstants.TASK_LOG_MSG, ip, JSONObject.toJSONString(msgResponse));
            if (StringUtils.isNotBlank(proxyComplete)) {
                nettyChannelUtils.pushRegisterByIp(ActionCodeConstants.TASK_LOG_MSG, proxyComplete, JSONObject.toJSONString(msgResponse));
            }
        } else if (jsonObject.containsKey("tempId")) {
            Integer tempId = jsonObject.getInteger("tempId");
            ChannelSupervise.addChannelByTempId(context.channel(), ActionCodeConstants.TASK_LOG_MSG, tempId);
        } else if (jsonObject.containsKey("pythonIp")) {
            String pythonIp = jsonObject.getString("pythonIp");
            ChannelSupervise.addChannelByActionAndIp(context.channel(), ActionCodeConstants.TASK_LOG_MSG, pythonIp);
//            log.info("ipChannelMap : "+ChannelSupervise.actionIpGlobalGroupMap);
        }

        return null;
    }
}
