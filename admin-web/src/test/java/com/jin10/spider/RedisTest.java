package com.jin10.spider;

import com.jin10.spider.common.utils.RedisUtils;
import com.jin10.spider.modules.template.entity.Template;
import com.jin10.spider.modules.template.service.ITemplateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hongda.fang
 * @date 2019-10-30 16:17
 * ----------------------------------------------
 */
@SpringBootTest
public class RedisTest {
    @Autowired
    private ITemplateService templateService;

    @Autowired
    RedisUtils redisUtils;


    @Test
    void findTemp() {
        List<Template> list = templateService.list();
        Map<String, Object> map = new HashMap<>();
        list.forEach(template -> {
            map.put(template.getId().toString(), template);
        });
        String key = "temp";

        Map<String, Template> hashMap = redisUtils.hget(key, Template.class);

        String a = "";
//        Map<Object, Object> byStatusMapRunning = templateService.findByStatusMapRunning();
//        String a = "";
    }
}
