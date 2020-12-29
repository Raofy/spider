package com.jin10.spider.modules.task.service;

import com.jin10.spider.common.utils.Constant;
import com.jin10.spider.common.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author hongda.fang
 * @date 2019-11-08 14:37
 * ----------------------------------------------
 * 项目启动后，跟随着启动
 */

@Component
@Order(1)
@Slf4j
public class TaskInitRunner implements CommandLineRunner {

    @Autowired
    IIpProxyProcessService proxyProcessService;
    @Autowired
    private IProductTaskService productTaskService;

    @Override
    public void run(String... args) throws Exception {

        log.warn(Constants.LOGGER_STRING + " start run creat task... ");

        long l = System.currentTimeMillis();
        //1.初始化任务模板
        productTaskService.initRunningTemplate();
        long e = System.currentTimeMillis();
        log.info("time = " + (e - l) + "ms");
        //2.检查高质量代理Ip
        proxyProcessService.checkIp(Constant.TEMPLATE.PROXY_LEVEL_HIGH, false);

        log.warn("初始化项目成功！！！ ");

    }

}
