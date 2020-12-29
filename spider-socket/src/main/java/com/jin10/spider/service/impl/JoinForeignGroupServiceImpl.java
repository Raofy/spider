package com.jin10.spider.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.jin10.spider.bean.IpDispatch;
import com.jin10.spider.cache.GlobalCache;
import com.jin10.spider.common.annotation.ActionCode;
import com.jin10.spider.common.constants.ActionCodeConstants;
import com.jin10.spider.common.constants.RedisKey;
import com.jin10.spider.common.netty.global.ChannelSupervise;
import com.jin10.spider.common.netty.interf.IActionSocketService;
import com.jin10.spider.common.utils.RedisUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author Airey
 * @date 2020/3/20 16:52
 * ----------------------------------------------
 * TODO
 * ----------------------------------------------
 */
@Service
@ActionCode(value = ActionCodeConstants.TASK_PUSH_FOREIGN)
@Slf4j
public class JoinForeignGroupServiceImpl implements IActionSocketService {

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public Object doAction(ChannelHandlerContext context, String message) {

        log.info("收到国外服务器消息分组请求: " + message);
        JSONObject jsonObject = JSONObject.parseObject(message);
        String ip = jsonObject.getString("ip");
        if (StringUtils.isNotBlank(ip)) {
            synchronized (this) {
                ChannelSupervise.addChannelByActionAndIp(context.channel(), ActionCodeConstants.TASK_PUSH_FOREIGN, ip);
                ChannelSupervise.timeForeignMap.put(ip, 999L);
                redisUtils.zAdd(RedisKey.SPIDER_NODE_FOREIGN_ZSET, ip, 999L);
//                GlobalCache.ipForeignDispatchQueue.offer(new IpDispatch(ip, 999L));

            }
        }
        Map<String, Channel> ipChannelMap = ChannelSupervise.actionIpGlobalGroupMap.get(ActionCodeConstants.TASK_PUSH_FOREIGN);
        log.info("国外服务器！！！ ipChannelMap = " + ipChannelMap.keySet() + ", timeForeignMap = " + ChannelSupervise.timeForeignMap.keySet() + ", ipForeignDispatchQueue = " + redisUtils.zRange(RedisKey.SPIDER_NODE_FOREIGN_ZSET, 0, -1));
        Channel channel = ipChannelMap.get(ip);
        log.info("国外服务器！！！ channel =" + channel);
        if (ObjectUtil.isNotNull(channel)) {
            channel.writeAndFlush(new TextWebSocketFrame("ip : " + ip + " 已经成功加入到组中！"));
        }
        return null;
    }

}
