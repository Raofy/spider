package com.jin10.spider.modules.statistics.handler;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONObject;
import com.jin10.spider.common.bean.MsgResponse;
import com.jin10.spider.common.bean.UrlTask;
import com.jin10.spider.common.constants.ActionCodeConstants;
import com.jin10.spider.common.enums.MsgCodeEnum;
import com.jin10.spider.common.netty.global.ChannelSupervise;
import com.jin10.spider.common.netty.global.ClientChannelGroup;
import com.jin10.spider.modules.statistics.bean.ServerInfo;
import com.jin10.spider.modules.statistics.bean.TaskLog;
import com.jin10.spider.modules.statistics.dto.GroupAreaIpInfoDto;
import com.jin10.spider.modules.statistics.dto.TaskQueueInfoDto;
import com.jin10.spider.modules.statistics.request.GetIpProxyUseRequest;
import com.jin10.spider.modules.template.service.ITemplateService;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.jin10.spider.common.netty.global.ClientChannelGroup.clientGroup;


/**
 * @author hongda.fang
 * @date 2019-12-12 10:39
 * ----------------------------------------------
 * 发送消息给前端
 */
@Component
public class PushWebMsgSocketHandler {

    @Autowired
    private ITemplateService templateService;

    private Logger logger = LoggerFactory.getLogger(getClass());


    /**
     * 广播 订阅的 action 的渠道
     *
     * @param registerAction
     * @param codeEnum
     * @param entity
     */
    private void pushRegisterActionChannel(int registerAction, MsgCodeEnum codeEnum, Object entity) {
        /**
         * 检查该去掉是否有 ChannelGroup，有则发送
         */
        if (ChannelSupervise.existActionChannelGroup(registerAction)) {
            MsgResponse msgResponse = new MsgResponse(codeEnum, entity);
            ChannelSupervise.send2AllAction(registerAction, msgResponse);
        }
    }


    /**
     * 通过action转发消息到socket服务端
     *
     * @param action
     * @param codeEnum
     * @param entity
     */
    private void transformMsgByAction(int action, MsgCodeEnum codeEnum, Object entity) {


        MsgResponse msgResponse = new MsgResponse(codeEnum, entity);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", action);
        jsonObject.put("clientMsg", msgResponse);
        jsonObject.put("ip", "127.0.0.1");

        String transformMsg = jsonObject.toJSONString();

        if (CollUtil.isNotEmpty(clientGroup)) {
            clientGroup.writeAndFlush(new TextWebSocketFrame(transformMsg));
        }


    }


    /**
     * 实时推送 爬虫服务器信息 给 web
     *
     * @param entity
     */
    public void pushServiceInfo(ServerInfo entity) {
        pushRegisterActionChannel(ActionCodeConstants.GET_SERVER_INFOS, MsgCodeEnum.SERVER_INFO, entity);
    }

    /**
     * 转发爬虫服务器信息给socket服务器
     *
     * @param entity
     */
    public void transformServerInfo(ServerInfo entity) {
        transformMsgByAction(ActionCodeConstants.GET_SERVER_INFOS, MsgCodeEnum.SERVER_INFO, entity);
    }


    /**
     * 转发代理Ip信息给socket服务器
     *
     * @param stringListMap
     */
    public void transformGetProxyIp(List<GroupAreaIpInfoDto> stringListMap) {
        transformMsgByAction(ActionCodeConstants.GET_PROXY_IP, MsgCodeEnum.PROXY_IP_LIST, stringListMap);
    }


    /**
     * 转发可用的代理ip给socket服务器
     *
     * @param taskLog
     */
    public void transformProxyIpUse(TaskLog taskLog) {
        String proxy = taskLog.getProxy();
        if (StringUtils.isNotBlank(proxy)) {
            GetIpProxyUseRequest proxyUseRequest = new GetIpProxyUseRequest();
            proxyUseRequest.setAction(ActionCodeConstants.GET_PROXY_IP_USE);
            String[] split = proxy.split(":");
            if (split.length == 2) {
                proxyUseRequest.setProxyIp(split[0]);
                taskLog.setProxy(split[0]);
                taskLog.setProxyPort(split[1]);
            } else {
                proxyUseRequest.setProxyIp(proxy);
            }
            transformMsgByAction(ActionCodeConstants.GET_PROXY_IP_USE, MsgCodeEnum.PROXY_IP_USE, taskLog);


        }

    }


    /**
     * 首次 推送队列产生的任务
     *
     * @param tasks
     */
    public void pushProductTask(List<UrlTask> tasks) {
        pushRegisterActionChannel(ActionCodeConstants.TASK_QUEUE_INFO, MsgCodeEnum.TASK_QUEUE_PRODUCT, tasks);
    }

    /**
     * 实时推送 队列信息
     */
    public void pushTaskQueueInfo() {
        List<TaskQueueInfoDto> taskQueueInfos = templateService.getTaskQueueInfos();
        pushRegisterActionChannel(ActionCodeConstants.TASK_QUEUE_INFO, MsgCodeEnum.TASK_QUEUE, taskQueueInfos);
    }


    /**
     * 推送检查ip监控信息
     *
     * @param stringListMap
     */
    public void pushProxyIps(List<GroupAreaIpInfoDto> stringListMap) {
        pushRegisterActionChannel(ActionCodeConstants.GET_PROXY_IP, MsgCodeEnum.PROXY_IP_LIST, stringListMap);
    }


    public void pushIpUse(TaskLog taskLog) {
        if (taskLog != null) {
            String proxy = taskLog.getProxy();
            if (StringUtils.isNotBlank(proxy)) {
                GetIpProxyUseRequest proxyUseRequest = new GetIpProxyUseRequest();
                proxyUseRequest.setAction(ActionCodeConstants.GET_PROXY_IP_USE);
                String[] split = proxy.split(":");
                if (split.length == 2) {
                    proxyUseRequest.setProxyIp(split[0]);
                    taskLog.setProxy(split[0]);
                    taskLog.setProxyPort(split[1]);
                } else {
                    proxyUseRequest.setProxyIp(proxy);
                }
                if (ChannelSupervise.existObjectChannelGroup(proxyUseRequest)) {
                    MsgResponse msgResponse = new MsgResponse(MsgCodeEnum.PROXY_IP_USE, taskLog);
                    ChannelSupervise.send2AllAction(proxyUseRequest, msgResponse);
                }
            }
        }

    }


}
