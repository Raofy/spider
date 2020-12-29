package com.jin10.spider.spiderserver.service.impl;

import com.jin10.spider.spiderserver.TestListener;
import com.jin10.spider.spiderserver.service.ITemplateService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;


/**
 * @author Airey
 * @date 2020/6/3 17:12
 * ----------------------------------------------
 * TODO
 * ----------------------------------------------
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@TestExecutionListeners(listeners = {TestListener.class, DependencyInjectionTestExecutionListener.class})
public class TemplateServiceImplTest {


    @Autowired
    private ITemplateService templateService;

    @Test
    public void findSiteByTempId() {
        String site = templateService.findSiteByTempId(345L);
        System.out.println(site);
    }
}