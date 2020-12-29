/*
package com.jin10.spider.spiderserver.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


*/
/**
 * @author Airey
 * @date 2019/11/13 16:15
 * ----------------------------------------------
 * rabbitmqConfig配置
 * ----------------------------------------------
 *//*

@Configuration
public class RabbitmqConfig {

    @Autowired
    ConnectionFactory connectionFactory;

    public static final String SPIDER_QUEUE = "spiderResult";

    @Bean
    public Queue spiderQueue() {
        return new Queue(SPIDER_QUEUE, false);
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(1);
        return factory;
    }

}
*/
