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
 * @date 2020/1/17 17:32
 * ----------------------------------------------
 * 爬虫消息推送标志，加入到分组中
 * ----------------------------------------------
 */
@Service
@ActionCode(value = ActionCodeConstants.TASK_PUSH)
@Slf4j
public class JoinGroupServiceImpl implements IActionSocketService {

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public Object doAction(ChannelHandlerContext context, String message) {

        log.info("收到国内消息分组请求: " + message);
        JSONObject jsonObject = JSONObject.parseObject(message);
        String ip = jsonObject.getString("ip");
        if (StringUtils.isNotBlank(ip)) {
            synchronized (this) {
                ChannelSupervise.addChannelByActionAndIp(context.channel(), ActionCodeConstants.TASK_PUSH, ip);
                ChannelSupervise.timeMap.put(ip, 999L);
                redisUtils.zAdd(RedisKey.SPIDER_NODE_ZSET, ip, 999L);
            }
        }
        Map<String, Channel> ipChannelMap = ChannelSupervise.actionIpGlobalGroupMap.get(ActionCodeConstants.TASK_PUSH);
        log.info("国内服务器！！！ ipChannelMap = " + ipChannelMap.keySet() + ", timeMap = " + ChannelSupervise.timeMap.keySet() + ", ipDispatchQueue = " + redisUtils.zRange(RedisKey.SPIDER_NODE_ZSET, 0, -1));
        Channel channel = ipChannelMap.get(ip);
        log.info("国内服务器！！！channel = " + channel);
        if (ObjectUtil.isNotNull(channel)) {
            channel.writeAndFlush(new TextWebSocketFrame("ip : " + ip + " 已经成功加入到国内分组中！"));
        }
        return null;
    }

}
