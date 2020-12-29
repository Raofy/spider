package com.jin10.spider.modules.statistics.controller;


import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jin10.spider.common.bean.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Airey
 * @date 2020/2/6 16:15
 * ----------------------------------------------
 * rabbitmq监控restful api调用
 * ----------------------------------------------
 */
@RestController
@RequestMapping("rabbitmq/monitor")
@Slf4j
public class RabbitmqMonitorRestController {

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    @Value("${spring.rabbitmq.host}")
    private String host;

    /**
     * 查询rabbitmq中所有的队列具体监控信息
     *
     * @return
     */
    @GetMapping("api/queues")
    public BaseResponse getQueues() {

        StringBuffer httpUrl = getUrlPrefix();
        httpUrl.append("/api/queues");

        HttpResponse execute = HttpRequest.get(httpUrl.toString()).basicAuth(username, password).execute();
        JSONArray array = new JSONArray();
        if (execute.isOk()) {
            String body = execute.body();
            if (JSONUtil.isJson(body)) {
                array = JSONObject.parseArray(body);
            }
        }

        return BaseResponse.ok(array);
    }


    /**
     * 查询rabbitmq总体概览信息
     *
     * @return
     */
    @GetMapping("api/overview")
    public BaseResponse getOverView() {

        StringBuffer url = getUrlPrefix();
        url.append("/api/overview");

        HttpResponse execute = HttpRequest.get(url.toString()).basicAuth(username, password).execute();
        JSONObject jsonObject = new JSONObject();
        if (execute.isOk()) {
            String body = execute.body();
            if (JSONUtil.isJson(body)) {
                jsonObject = JSONObject.parseObject(body);
            }
        }

        return BaseResponse.ok(jsonObject);

    }


    /**
     * 查询rabbitmq连接监控信息
     */
    @GetMapping("api/connections")
    public BaseResponse getConnections() {

        StringBuffer url = getUrlPrefix();
        url.append("/api/connections");

        HttpResponse execute = HttpRequest.get(url.toString()).basicAuth(username, password).execute();
        JSONArray array = new JSONArray();
        if (execute.isOk()) {
            String body = execute.body();
            if (JSONUtil.isJson(body)) {
                array = JSONObject.parseArray(body);
            }
        }
        return BaseResponse.ok(array);
    }


    /**
     * 查询rabbitmq的所有节点监控信息
     * @return
     */
    @GetMapping("api/nodes")
    public BaseResponse getNodes(){

        StringBuffer url = getUrlPrefix();
        url.append("/api/nodes");

        HttpResponse execute = HttpRequest.get(url.toString()).basicAuth(username, password).execute();
        JSONArray array = new JSONArray();
        if (execute.isOk()) {
            String body = execute.body();
            if (JSONUtil.isJson(body)) {
                array = JSONObject.parseArray(body);
            }
        }
        return BaseResponse.ok(array);
    }


    /**
     * 封装url前缀
     *
     * @return
     */
    private StringBuffer getUrlPrefix() {
        StringBuffer httpUrl = new StringBuffer();
        httpUrl.append("http://");
        httpUrl.append(host);
        httpUrl.append(":15672");
        return httpUrl;
    }

}
