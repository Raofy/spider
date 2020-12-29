package com.jin10.spider.spiderserver.mainThread;

import com.jin10.spider.common.netty.service.NioWebSocketServer;
import com.jin10.spider.spiderserver.constants.DataCache;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author Airey
 * @date 2019/11/19 14:24
 * ----------------------------------------------
 * Netty服务线程启动
 * ----------------------------------------------
 */
@Component
@Slf4j
public class NettyServerThread implements CommandLineRunner {

    @Autowired
    private NioWebSocketServer nettyServer;

    @Value("${jin10.socket.port}")
    private Integer port;


    @Override
    public void run(String... args) throws Exception {
        log.warn("websocket服务器准备启动！ 端口为 :  " + port);
        nettyServer.init(port);
        return;
    }


}
