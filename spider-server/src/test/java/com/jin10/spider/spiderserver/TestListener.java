package com.jin10.spider.spiderserver;

import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

/**
 * @author Airey
 * @date 2020/6/3 17:26
 * ----------------------------------------------
 * TODO
 * ----------------------------------------------
 */
public class TestListener implements TestExecutionListener {


    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }
}
