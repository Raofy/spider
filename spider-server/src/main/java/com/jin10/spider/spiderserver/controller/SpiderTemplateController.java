package com.jin10.spider.spiderserver.controller;

import cn.hutool.core.map.MapUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.jin10.spider.common.bean.BaseResponse;
import com.jin10.spider.common.service.DingTalkClient;
import com.jin10.spider.spiderserver.config.CustomConfig;
import com.jin10.spider.spiderserver.entity.PreUser;
import com.jin10.spider.spiderserver.utils.PreUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static com.jin10.spider.spiderserver.constants.GlobalConstants.DISPATCH_PREFIX;

/**
 * @author Airey
 * @date 2019/12/6 14:40
 * ----------------------------------------------
 * 模板管理,做权限校验,请求转发
 * ----------------------------------------------
 */
@RestController
@Slf4j
@RequestMapping("admin")
public class SpiderTemplateController {

    @Value("${jwt.header}")
    private String tokenHeader;

    @Value("${jwt.tokenHead}")
    private String authTokenStart;

    @Autowired
    private DingTalkClient dingTalkClient;

    @Autowired
    private CustomConfig customConfig;


    /**
     * 新增,修改模板
     *
     * @param paramMap
     * @return
     */
    @PostMapping("template/saveOrUpdate")
    public BaseResponse saveOrUpdate(@RequestBody Map<String, Object> paramMap, HttpServletRequest request) {
        String token = getToken(request);
        String realUrl = customConfig.getAdminUrl() + "/admin/template/saveOrUpdate";
        BaseResponse response = getBaseResponse(realUrl, paramMap, token);
        if (response.getCode() == HttpStatus.HTTP_OK) {

            String montiorTitle = "";
            String pageSite = "";
            String channel = "";
            String title;
            String text;
            try {
                if (response.getData() instanceof JSONObject) {
                    montiorTitle = ((JSONObject) response.getData()).getString("title");
                    pageSite = ((JSONObject) response.getData()).getString("pageSite");
                    channel = ((JSONObject) response.getData()).getString("channel");
                }
            } catch (Exception e) {
                log.error("监控端数据异常 ", e);
            }

            PreUser preUser = PreUtil.getCurrentUser();

            if (paramMap.containsKey("id")) {
                title = "更新监控源";
                text = DISPATCH_PREFIX + "！ " + preUser.getUsername() + " 更新了监控源, title = " + montiorTitle + " channel = " + channel;
            } else {
                title = "新增监控源";
                text = DISPATCH_PREFIX + "！ " + preUser.getUsername() + " 新增了监控源, title = " + montiorTitle + " channel = " + channel;
            }

            StringBuffer disMsg = new StringBuffer();
            disMsg.append(text + "\n");
            disMsg.append("\r\n");
            disMsg.append("***\n");
            disMsg.append("[当前监控信息]");
            disMsg.append("(");
            disMsg.append(realUrl);
            disMsg.append(")\n");
            disMsg.append("\r\n");
            disMsg.append("***\n");
            disMsg.append("[打开目标页面]");
            disMsg.append("(");
            disMsg.append(pageSite);
            disMsg.append(")");

            dingTalkClient.sendMarkDown(customConfig.getHttpDispatchUrl(), title, disMsg.toString());

        }
        return response;
    }


    /**
     * 删除模板
     *
     * @param paramMap
     * @return
     */
    @PostMapping("template/delete")
    public BaseResponse delete(@RequestBody Map<String, Object> paramMap, HttpServletRequest request) {
        String token = getToken(request);
        String realUrl = customConfig.getAdminUrl() + "/admin/template/delete";
        BaseResponse response = getBaseResponse(realUrl, paramMap, token);
        if (response.getCode() == HttpStatus.HTTP_OK) {
            boolean status = (boolean) response.getData();
            if (status) {

                String title = "删除监控源";
                String channel = MapUtil.getStr(paramMap, "channel");
                String montiorTitle = MapUtil.getStr(paramMap, "title");
                String text = DISPATCH_PREFIX + "！ " + PreUtil.getCurrentUser().getUsername() + " 删除了监控源, title = " + montiorTitle + " , channel = " + channel;

                StringBuffer disMsg=new StringBuffer();
                disMsg.append(text + "\n");
                disMsg.append("\r\n");
                disMsg.append("***\n");
                disMsg.append("[当前监控信息]");
                disMsg.append("(");
                disMsg.append(realUrl);
                disMsg.append(")\n");
                disMsg.append("\r\n");
                disMsg.append("***\n");
                disMsg.append("[打开目标页面]");
                disMsg.append("(");
                disMsg.append(MapUtil.getStr(paramMap, "pageSite"));
                disMsg.append(")");

                dingTalkClient.sendMarkDown(customConfig.getHttpDispatchUrl(), title, disMsg.toString());
            }
        }
        return response;

    }

    /**
     * 删除模板
     *
     * @param paramMap
     * @return
     */
    @PostMapping("template/stop")
    public BaseResponse stop(@RequestBody Map<String, Object> paramMap, HttpServletRequest request) {
        String token = getToken(request);
        String realUrl = customConfig.getAdminUrl() + "/admin/template/stop";
        BaseResponse response = getBaseResponse(realUrl, paramMap, token);
        if (response.getCode() == HttpStatus.HTTP_OK) {
            boolean status = (boolean) response.getData();
            if (status) {
                String title = "停止监控源";
                String channel = MapUtil.getStr(paramMap, "channel");
                String montiorTitle = MapUtil.getStr(paramMap, "title");
                String text = DISPATCH_PREFIX + "！ " + PreUtil.getCurrentUser().getUsername() + " 停止了监控源, title = " + montiorTitle + " , channel = " + channel;

                StringBuffer disMsg=new StringBuffer();
                disMsg.append(text + "\n");
                disMsg.append("\r\n");
                disMsg.append("***\n");
                disMsg.append("[当前监控信息]");
                disMsg.append("(");
                disMsg.append(realUrl);
                disMsg.append(")\n");
                disMsg.append("\r\n");
                disMsg.append("***\n");
                disMsg.append("[打开目标页面]");
                disMsg.append("(");
                disMsg.append(MapUtil.getStr(paramMap, "pageSite"));
                disMsg.append(")");

                dingTalkClient.sendMarkDown(customConfig.getHttpDispatchUrl(), title, disMsg.toString());
            }
        }
        return response;
    }


    /**
     * 获取返回响应体
     *
     * @param realUrl
     * @param paramMap
     * @return
     */
    private BaseResponse getBaseResponse(String realUrl, Map<String, Object> paramMap, String token) {
        BaseResponse response = new BaseResponse();
        HttpResponse execute = HttpRequest.post(realUrl).body(JSONUtil.toJsonStr(paramMap)).header(tokenHeader, token).execute();
        if (execute.getStatus() == HttpStatus.HTTP_OK) {
            String body = execute.body();
            if (JSONObject.isValid(body)) {
                JSONObject resultJson = JSONObject.parseObject(body);
                response = resultJson.toJavaObject(BaseResponse.class);
            }
        } else {
            return BaseResponse.error(execute.getStatus(), execute.body());
        }
        return response;
    }


    /**
     * 获取请求token
     *
     * @param request
     * @return
     */
    private String getToken(HttpServletRequest request) {
        String token = request.getHeader(tokenHeader);
        if (StringUtils.isNotEmpty(token)) {
            token = token.substring(authTokenStart.length());
        }
        return token;
    }

}
