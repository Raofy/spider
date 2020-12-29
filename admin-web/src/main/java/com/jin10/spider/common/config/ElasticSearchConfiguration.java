package com.jin10.spider.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;


/**
 * @author hongda.fang
 * @date 2019-12-04 17:56
 * ----------------------------------------------
 * 解决SpringBoot netty与ES netty 相关jar冲突
 */
@Component
public class ElasticSearchConfiguration implements InitializingBean {


    private Logger logger = LoggerFactory.getLogger(getClass());


    static {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        logger.warn( "es.set.netty.runtime.available.processors  " +  System.getProperty("es.set.netty.runtime.available.processors"));
    }
}
