package com.jin10.spider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class AdminWebApplication {

	public static void main(String[] args) {
		System.setProperty("es.set.netty.runtime.available.processors", "false");
		SpringApplication.run(AdminWebApplication.class, args);
		System.out.println("============================= 启动成功 ============================");
	}



}
