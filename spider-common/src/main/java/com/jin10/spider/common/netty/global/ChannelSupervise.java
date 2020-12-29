package com.jin10.spider.common.netty.global;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.HashBiMap;
import com.jin10.spider.common.constants.ActionCodeConstants;
import com.jin10.spider.common.constants.RedisKey;
import com.jin10.spider.common.utils.JsonUtils2;
import com.jin10.spider.common.utils.RedisUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class ChannelSupervise {

    @Autowired
    private RedisUtils redisUtils;

    public static ChannelGroup GlobalGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    public static ConcurrentMap<String, ChannelId> ChannelMap = new ConcurrentHashMap();
    public static ConcurrentHashMap<Object, ChannelGroup> actionGlobalGroupMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, Long> timeMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, Long> timeForeignMap = new ConcurrentHashMap<>();


    public static ConcurrentHashMap<Integer, Map<String, Channel>> actionIpGlobalGroupMap = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<Integer, Map<Integer, Channel>> actionTempIdGlobalGroupMap = new ConcurrentHashMap<>();


    public static void addChannel(Channel channel) {
        GlobalGroup.add(channel);
        ChannelMap.put(channel.id().asShortText(), channel.id());
    }

    public static void removeChannel(Channel channel) {
        GlobalGroup.remove(channel);
        ChannelMap.remove(channel.id().asShortText());
        removeChannelByActionCode(channel);
        removeChannelByIp(channel);
        removeChannelByTempId(channel);
    }

    public static Channel findChannel(String id) {
        return GlobalGroup.find(ChannelMap.get(id));
    }

    public static void send2All(TextWebSocketFrame tws) {
        GlobalGroup.writeAndFlush(tws);
    }


    public static void addChannelByActionCode(Channel channel, int actionCode) {
        ChannelGroup channels = actionGlobalGroupMap.get(actionCode);
        if (channels == null) {
            channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
        }
        channels.add(channel);
        actionGlobalGroupMap.put(actionCode, channels);
        addChannelByObject(channel, actionCode);
    }

    public static void addChannelByActionAndIp(Channel channel, int actionCode, String ip) {
        Map<String, Channel> ipChannelMap = actionIpGlobalGroupMap.get(actionCode);
        if (CollUtil.isEmpty(ipChannelMap)) {
            ipChannelMap = new ConcurrentHashMap<>();
        }
        ipChannelMap.put(ip, channel);
        actionIpGlobalGroupMap.put(actionCode, ipChannelMap);
    }

    /**
     * 通过模板id加入channel
     *
     * @param channel
     * @param actionCode
     * @param tempId
     */
    public static void addChannelByTempId(Channel channel, int actionCode, int tempId) {

        Map<Integer, Channel> tempIdChannelMap = actionTempIdGlobalGroupMap.get(actionCode);
        if (CollUtil.isEmpty(tempIdChannelMap)) {
            tempIdChannelMap = new HashMap<>();
        }
        tempIdChannelMap.put(tempId, channel);
        actionTempIdGlobalGroupMap.put(actionCode, tempIdChannelMap);
    }


    public static void addChannelByObject(Channel channel, Object object) {
        ChannelGroup channels = actionGlobalGroupMap.get(object);
        if (channels == null) {
            channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
        }
        channels.add(channel);
        actionGlobalGroupMap.put(object, channels);
    }


    public static void removeChannelByActionCode(Channel channel) {
        for (Object actionCode : actionGlobalGroupMap.keySet()) {
            ChannelGroup channels = actionGlobalGroupMap.get(actionCode);
            removeChannel(channels, channel);
        }
    }


    /**
     * 通过Ip移除对应的channel
     *
     * @param channel
     */
    public static void removeChannelByIp(Channel channel) {

        for (Integer actionCode : actionIpGlobalGroupMap.keySet()) {

            if (actionCode == ActionCodeConstants.TASK_PUSH) {
                Map<String, Channel> ipChannelMap = actionIpGlobalGroupMap.get(actionCode);
                if (!CollUtil.isEmpty(ipChannelMap)) {
                    HashBiMap<String, Object> biMap = HashBiMap.create();
                    biMap.putAll(ipChannelMap);
                    if (ipChannelMap.containsValue(channel)) {
                        ipChannelMap.remove(biMap.inverse().get(channel));
                        actionIpGlobalGroupMap.put(ActionCodeConstants.TASK_PUSH, ipChannelMap);
                        timeMap.remove(biMap.inverse().get(channel));
                    }
                }

            } else if (actionCode == ActionCodeConstants.TASK_PUSH_FOREIGN) {
                Map<String, Channel> ipChannelMap = actionIpGlobalGroupMap.get(actionCode);
                if (!CollUtil.isEmpty(ipChannelMap)) {
                    HashBiMap<String, Object> biMap = HashBiMap.create();
                    biMap.putAll(ipChannelMap);
                    if (ipChannelMap.containsValue(channel)) {
                        ipChannelMap.remove(biMap.inverse().get(channel));
                        actionIpGlobalGroupMap.put(ActionCodeConstants.TASK_PUSH_FOREIGN, ipChannelMap);
                        timeForeignMap.remove(biMap.inverse().get(channel));
                    }
                }
            } else {
                Map<String, Channel> ipChannelMap = actionIpGlobalGroupMap.get(actionCode);
                if (!CollUtil.isEmpty(ipChannelMap)) {
                    HashBiMap<String, Object> biMap = HashBiMap.create();
                    biMap.putAll(ipChannelMap);
                    if (ipChannelMap.containsValue(channel)) {
                        ipChannelMap.remove(biMap.inverse().get(channel));
                    }
                }
            }

        }

    }



    /**
     * 通过tempId移除对应的channel
     *
     * @param channel
     */
    public static void removeChannelByTempId(Channel channel) {
        Map<Integer, Channel> tempIdChannelMap = actionTempIdGlobalGroupMap.get(ActionCodeConstants.TASK_LOG_MSG);
        if (!CollUtil.isEmpty(tempIdChannelMap)) {
            HashBiMap<Integer, Object> biMap = HashBiMap.create();
            biMap.putAll(tempIdChannelMap);
            if (tempIdChannelMap.containsValue(channel)) {
                tempIdChannelMap.remove(biMap.inverse().get(channel));
                actionTempIdGlobalGroupMap.put(ActionCodeConstants.TASK_LOG_MSG, tempIdChannelMap);
            }
        }
    }

    private static void removeChannel(ChannelGroup channels, Channel channel) {
        if (channels != null && channel != null) {
            channels.remove(channel);
            channels.remove(channel.id().asShortText());
        }
    }


    public static void removeByObjectChannel(Channel channel, Object object) {
        ChannelGroup channels = actionGlobalGroupMap.get(object);
        removeChannel(channels, channel);
    }


    public static void send2AllAction(Object action, Object entity) {
        ChannelGroup channels = actionGlobalGroupMap.get(action);
        if (channels != null) {
            channels.writeAndFlush(new TextWebSocketFrame(JsonUtils2.writeValue(entity)));
        }
    }


    public static void send2AllAction(Object action, Object entity, boolean flag) {
        ChannelGroup channels = actionGlobalGroupMap.get(action);
        if (channels != null) {
            channels.writeAndFlush(new TextWebSocketFrame(JSONObject.toJSONString(entity)));
        }
    }


    public static void send2AllAction(Object action, String msg) {
        ChannelGroup channels = actionGlobalGroupMap.get(action);
        if (channels != null) {
            channels.writeAndFlush(new TextWebSocketFrame(msg));
        }

    }


    public static boolean existActionChannelGroup(int action) {
        return CollUtil.isNotEmpty(actionGlobalGroupMap.get(action));
    }


    public static boolean existObjectChannelGroup(Object object) {
        return CollUtil.isNotEmpty(actionGlobalGroupMap.get(object));
    }


}
