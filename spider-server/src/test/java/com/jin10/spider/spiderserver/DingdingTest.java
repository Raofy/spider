package com.jin10.spider.spiderserver;

import cn.hutool.http.HttpUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.util.WebUtils;

/**
 * @author Airey
 * @date 2019/12/2 11:33
 * ----------------------------------------------
 * TODO
 * ----------------------------------------------
 */

public class DingdingTest {

    public static void main(String[] args) {

        String httpUrl="https://oapi.dingtalk.com/robot/send?access_token=bd2012d22ea5ecb30e556d16ccacf46e98361af67573a48b3017c33dd8adc475";

        String json="{\n" +
                "    \"msgtype\": \"text\", \n" +
                "    \"text\": {\n" +
                "        \"content\": \"类别不匹配,我就是我, 是不一样的烟火\"\n" +
                "    }, \n" +
                "    \"at\": {\n" +
                "        \"atMobiles\": [\n" +
                "            \"18883179959\", \n" +
                "        ], \n" +
                "        \"isAtAll\": false\n" +
                "    }\n" +
                "}";

        String post = HttpUtil.post(httpUrl, json);
        System.out.println(post);

    }

}
