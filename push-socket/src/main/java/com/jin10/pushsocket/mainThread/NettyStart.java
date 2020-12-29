package com.jin10.pushsocket.mainThread;

import com.jin10.pushsocket.server.NioWebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author Airey
 * @date 2020/5/29 14:51
 * ----------------------------------------------
 * TODO
 * ----------------------------------------------
 */
@Component
@Slf4j
public class NettyStart implements CommandLineRunner {

    @Autowired
    private NioWebSocketServer nettyServer;

    @Value("${jin10.socket.port}")
    private Integer port;


    @Override
    public void run(String... args) throws Exception {

        log.warn("websocket服务器准备启动！ 端口为 :  " + port);

        nettyServer.init(port);

    }
}
