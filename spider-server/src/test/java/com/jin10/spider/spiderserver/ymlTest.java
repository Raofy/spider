package com.jin10.spider.spiderserver;

import com.jin10.spider.spiderserver.config.CustomConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Airey
 * @date 2019/12/9 14:23
 * ----------------------------------------------
 * TODO
 * ----------------------------------------------
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ymlTest {

    @Autowired
    private CustomConfig customConfig;

    @Test
    public void testConfig(){

        System.out.println(customConfig);

    }

}
