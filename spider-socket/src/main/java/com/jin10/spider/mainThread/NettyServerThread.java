package com.jin10.spider.mainThread;

import com.jin10.spider.common.constants.RedisKey;
import com.jin10.spider.common.netty.service.NioWebSocketServer;
import com.jin10.spider.common.utils.RedisUtils;
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
    @Autowired
    private RedisUtils redisUtils;

    @Value("${jin10.socket.port}")
    private Integer port;

    @Value("${custom.admin.httpUrl}")
    private String url;

    @Override
    public void run(String... args) throws Exception {

        redisUtils.del(RedisKey.SPIDER_NODE_ZSET);
        redisUtils.del(RedisKey.SPIDER_NODE_FOREIGN_ZSET);

        log.warn("websocket服务器准备启动！ 端口为 :  " + port);
        log.info("url =" + url);
        nettyServer.init(port);
        return;
    }


}
