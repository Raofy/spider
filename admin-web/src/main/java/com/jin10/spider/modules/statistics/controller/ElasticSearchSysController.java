package com.jin10.spider.modules.statistics.controller;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jin10.spider.common.bean.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Airey
 * @date 2019/12/27 16:59
 * ----------------------------------------------
 * elasticsearch 系统信息
 * ----------------------------------------------
 */
@RestController
@RequestMapping("/es")
@Slf4j
public class ElasticSearchSysController {


    @GetMapping("getAllIndexInfo")
    public BaseResponse getAllIndexInfo() {

        String url = "http://172.16.30.177:9200/_cat/indices?format=json";
        HttpResponse execute = HttpRequest.get(url).execute();
        JSONArray array = new JSONArray();
        if (execute.isOk()) {
            log.info("执行成功！");
            String body = execute.body();
            if (JSONUtil.isJson(body)) {
                array = JSONObject.parseArray(body);
            }

        }
        return BaseResponse.ok(array);

    }


}
