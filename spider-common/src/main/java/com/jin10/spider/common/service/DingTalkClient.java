package com.jin10.spider.common.service;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jin10.spider.common.bean.DispatchMsg;
import com.jin10.spider.common.bean.NoticeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hongda.fang
 * @date 2019-11-08 17:48
 * ----------------------------------------------
 * <p>
 * 钉钉 通知 客户端
 */
@Component
public class DingTalkClient {

    @Value("${spring.profiles.active}")
    String active;

    private Logger logger = LoggerFactory.getLogger(getClass());


    /**
     * 仅仅通知使用
     *
     * @param httpUrl
     * @param content
     * @return
     */
    public boolean sendByNotice(String httpUrl, String content) {

        JSONObject warnMsg = new JSONObject();
        warnMsg.put("msgtype", "text");
        Map<String, Object> contentMap = new HashMap<>();
        contentMap.put("content", content);
        warnMsg.put("text", contentMap);
        String warnMsgJson = warnMsg.toJSONString();
        logger.info("warnMsgJson：" + warnMsgJson);
        HttpResponse execute = HttpRequest.post(httpUrl).body(warnMsgJson).execute();
        return execute.getStatus() == HttpStatus.HTTP_OK;
    }

    /**
     * 通知指定消息接收人,包含前缀
     *
     * @param httpUrl   需要发送的url
     * @param content   发送的内容
     * @param phoneList 通知人列表
     * @return
     */
    public boolean sendByReceiver(String httpUrl, String content, List<String> phoneList) {
        JSONObject warnMsg = new JSONObject();
        warnMsg.put("msgtype", "text");
        Map<String, Object> contentMap = new HashMap<>();
        contentMap.put("content", content);
        warnMsg.put("text", contentMap);
        Map<String, Object> atMap = new HashMap<>();
        atMap.put("isAtAll", false);
        atMap.put("atMobiles", phoneList);
        warnMsg.put("at", atMap);
        HttpResponse execute = HttpRequest.post(httpUrl).body(warnMsg.toJSONString()).execute();
        return execute.getStatus() == HttpStatus.HTTP_OK;
    }

    /**
     * markdown通知形式 通知部分人
     *
     * @param httpUrl
     * @param content
     * @param phoneList
     * @return
     */
    public boolean sendMarkDown(String httpUrl, String title, String content, List<String> phoneList) {

        JSONObject warnMsg = new JSONObject();
        warnMsg.put("msgtype", "markdown");
        HashMap<String, Object> contentMap = new HashMap<>();
        contentMap.put("title", title);
        contentMap.put("text", content);
        warnMsg.put("markdown", contentMap);
        HashMap<String, Object> atMap = new HashMap<>();
        atMap.put("atMobiles", phoneList);
        atMap.put("isAtAll", false);
        warnMsg.put("at", atMap);
        HttpResponse execute = HttpRequest.post(httpUrl).body(warnMsg.toJSONString()).execute();
        return execute.getStatus() == HttpStatus.HTTP_OK;
    }

    /**
     * markdown通知形式 普通通知
     *
     * @param httpUrl
     * @param title
     * @param content
     * @return
     */
    public boolean sendMarkDown(String httpUrl, String title, String content) {

        JSONObject warnMsg = new JSONObject();
        warnMsg.put("msgtype", "markdown");
        HashMap<String, Object> contentMap = new HashMap<>();
        contentMap.put("title", title);
        contentMap.put("text", content);
        warnMsg.put("markdown", contentMap);
        HashMap<String, Object> atMap = new HashMap<>();
        atMap.put("atMobiles", null);
        atMap.put("isAtAll", false);
        warnMsg.put("at", atMap);
        HttpResponse execute = HttpRequest.post(httpUrl).body(warnMsg.toJSONString()).execute();
        return execute.getStatus() == HttpStatus.HTTP_OK;

    }


    /**
     * 通知群里所有人
     *
     * @param httpUrl 需要发送的url
     * @param content 发送的内容
     * @return
     */
    public boolean sendByAll(String httpUrl, String content) {
        JSONObject warnMsg = new JSONObject();
        warnMsg.put("msgtype", "text");
        Map<String, Object> contentMap = new HashMap<>();
        contentMap.put("content", content);
        warnMsg.put("text", contentMap);
        Map<String, Object> atMap = new HashMap<>();
        atMap.put("isAtAll", true);
        warnMsg.put("at", atMap);
        HttpResponse execute = HttpRequest.post(httpUrl).body(warnMsg.toJSONString()).execute();
        return execute.getStatus() == HttpStatus.HTTP_OK;
    }


    /**
     * 监控源变更通知管理
     *
     * @param httpUrl
     * @param dispatchMsg
     * @return
     */
    public boolean sendDispatchMsg(String httpUrl, DispatchMsg dispatchMsg) {

        JSONObject dispatchJson = new JSONObject();
        dispatchJson.put("msgtype", "actionCard");

        JSONObject item = new JSONObject();
        item.put("title", dispatchMsg.getTitle());
        item.put("text", dispatchMsg.getText());
        item.put("hideAvatar", "0");
        item.put("btnOrientation", "0");

        JSONArray btns = new JSONArray();

        NoticeType noticeType = new NoticeType();
        noticeType.setTitle("当前监控信息");
        noticeType.setActionURL(dispatchMsg.getMonitorUrl());

        NoticeType noticeType1 = new NoticeType();

        noticeType1.setTitle("打开目标页面");
        noticeType1.setActionURL(dispatchMsg.getTargetUrl());

        btns.add(noticeType);
        btns.add(noticeType1);

        item.put("btns", btns);

        dispatchJson.put("actionCard", item);

        HttpResponse execute = HttpRequest.post(httpUrl).body(dispatchJson.toJSONString()).execute();
        return execute.getStatus() == HttpStatus.HTTP_OK;


    }


    /**
     * 监控源变更通知管理
     *
     * @param httpUrl
     * @param dispatchMsg
     * @return
     */
    public boolean sendDispatchMsgStop(String httpUrl, DispatchMsg dispatchMsg) {

        JSONObject dispatchJson = new JSONObject();
        dispatchJson.put("msgtype", "actionCard");

        JSONObject item = new JSONObject();
        item.put("title", dispatchMsg.getTitle());
        item.put("text", dispatchMsg.getText());
        item.put("hideAvatar", "0");
        item.put("btnOrientation", "0");

        JSONArray btns = new JSONArray();

        NoticeType noticeType = new NoticeType();
        noticeType.setTitle("源快照");
        noticeType.setActionURL(dispatchMsg.getMonitorUrl());

        NoticeType noticeType1 = new NoticeType();
        noticeType1.setTitle("打开目标页面");
        noticeType1.setActionURL(dispatchMsg.getTargetUrl());

        btns.add(noticeType);
        btns.add(noticeType1);

        item.put("btns", btns);

        dispatchJson.put("actionCard", item);

        HttpResponse execute = HttpRequest.post(httpUrl).body(dispatchJson.toJSONString()).execute();
        return execute.getStatus() == HttpStatus.HTTP_OK;


    }

}
