package com.jin10.spider;

import com.jin10.spider.modules.template.service.ITemplateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author hongda.fang
 * @date 2019-11-15 12:03
 * ----------------------------------------------
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TaskTest {
    @Autowired
    private ITemplateService templateService;

    @Test
    public void saveTemp(){

        String a = "";


    }
}
