package com.jin10.spider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpiderSocketApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpiderSocketApplication.class, args);
    }

}
