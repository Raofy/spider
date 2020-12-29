package com.jin10.spider.utils;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Airey
 * @date 2020/4/16 10:20
 * ----------------------------------------------
 * 钉钉告警
 * ----------------------------------------------
 */
@Service
public class DingWarnUtils {


    @Value("${custom.admin.httpUrl}")
    private String adminUrl;

    /**
     * 发送钉钉告警
     *
     * @param warnMsg
     * @param type
     */
    public void sendWarnMsg(String warnMsg, String type) {
        String realUrl = adminUrl + "/admin/ding/websocket";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("warnMsg", warnMsg);
        paramMap.put("type", type);
        HttpRequest.post(realUrl).body(JSONUtil.toJsonStr(paramMap)).execute();
    }


}
