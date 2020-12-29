package com.jin10.spider.modules.template.service;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.jin10.spider.common.bean.TemplateDto;
import com.jin10.spider.common.exception.BaseException;
import com.jin10.spider.common.utils.JsonUtils2;
import com.jin10.spider.modules.task.dto.UrlTaskDto;
import com.jin10.spider.modules.template.dto.TestTempDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hongda.fang
 * @date 2019-12-16 11:04
 * ----------------------------------------------
 * 爬虫 请求客户端
 */
@Component
public class SpiderClient {
    private Logger logger = LoggerFactory.getLogger(getClass());


    @Value("${custom.spider.baseUrl}")
    public String spiderUrl;
    @Value("${custom.spider.foreignUrl}")
    private String foreignUrl;

    /**
     * 请求 测试模版，返回测试结果
     *
     * @param taskDto
     * @param isForeign
     * @return
     */
    public TestTempDto requestTestTemp(UrlTaskDto taskDto, boolean isForeign) {
        List<UrlTaskDto> dtos = new ArrayList<>();
        TemplateDto temp = taskDto.getTemp();
        if (temp != null){
            temp.setFilterBy(0);
        }
        dtos.add(taskDto);

        String requestBody = JsonUtils2.writeValue(taskDto);

        logger.warn("   requestTestTemp  " + requestBody);
        HttpResponse execute = null;
        if (isForeign) {
            /**
             * 国外
             */
            execute = HttpRequest.post(foreignUrl + "api/fetch").body(requestBody).timeout(60000).execute();
        } else {
            execute = HttpRequest.post(spiderUrl + "api/fetch").body(requestBody).timeout(60000).execute();
        }
        String body = execute.body();
        logger.warn(body);
        if (!JSONUtil.isJson(body)) {
            throw new BaseException("爬虫端返回" + body + "不为json字符串");
        }
        TestTempDto tempDto = JSONUtil.toBean(body, TestTempDto.class);

        return tempDto;
    }


}
