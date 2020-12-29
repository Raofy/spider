package com.jin10.spider.spiderserver;

import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Airey
 * @date 2019/11/19 10:26
 * ----------------------------------------------
 * TODO
 * ----------------------------------------------
 */
@SpringBootTest
public class JsonObjectTest {

    @Test
    public void testJsonObj() {
        String jsonStr = "{\n" +
                "    \"status\":0,\n" +
                "    \"time\":234242341231,\n" +
                "    \"taskId\":4324234123423,\n" +
                "    \"message\":\"fdasfdsjflkejrljweqrlwjeqrqwer\",\n" +
                "    \"data\":[\n" +
                "        {\n" +
                "            \"title\":\"金山办公董事长兼CEO葛珂：坚持技术立业，开启WPS的新时代\",\n" +
                "            \"url\":\"http://www.bejson.com/jsoneditoronline\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"title\":\"财政部提前下达专项扶贫资金1136亿元 助力脱贫攻坚\",\n" +
                "            \"url\":\"https://mbd.baidu.com/newspage/data/landingsuper?context=%7B%22nid%22%3A%22news_9433373114057040396%22%7D&n_type=0&p_from=1\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        try {
            System.out.println(jsonStr);
            JSONObject jsonObject = JSONObject.parseObject(jsonStr);
            System.out.println(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
