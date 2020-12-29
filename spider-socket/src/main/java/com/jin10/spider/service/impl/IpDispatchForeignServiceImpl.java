package com.jin10.spider.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.jin10.spider.common.annotation.ActionCode;
import com.jin10.spider.common.constants.ActionCodeConstants;
import com.jin10.spider.common.constants.RedisKey;
import com.jin10.spider.common.netty.global.ChannelSupervise;
import com.jin10.spider.common.netty.interf.IActionSocketService;
import com.jin10.spider.common.utils.RedisUtils;
import com.jin10.spider.utils.DingWarnUtils;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Airey
 * @date 2020/2/4 16:59
 * ----------------------------------------------
 * 根据返回的状态判断ip调度规则  国外服务器
 * ----------------------------------------------
 */
@Service
@ActionCode(value = ActionCodeConstants.CLIENT_FOREIGN_STATUS)
@Slf4j
public class IpDispatchForeignServiceImpl implements IActionSocketService {

    @Autowired
    private DingWarnUtils dingWarnUtils;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public Object doAction(ChannelHandlerContext context, String message) {
        log.info("收到国外ip状态消息为: " + message);
        JSONObject jsonObject = JSONObject.parseObject(message);
        String ip = jsonObject.getString("ip");
        int status = jsonObject.getInteger("status");

        switch (status) {

            case 0:
                redisUtils.zAdd(RedisKey.SPIDER_NODE_FOREIGN_ZSET, ip, System.currentTimeMillis() + 500);
                break;
            case 1:
                if (ObjectUtil.isNull(redisUtils.zScore(RedisKey.SPIDER_NODE_FOREIGN_ZSET, ip))) {
                    log.info("before timeForeignMap = " + ChannelSupervise.timeForeignMap + " , ipDisPatchForeignQueue = " + redisUtils.zRange(RedisKey.SPIDER_NODE_FOREIGN_ZSET, 0, -1));
                    ChannelSupervise.timeForeignMap.put(ip, 999L);
                    redisUtils.zAdd(RedisKey.SPIDER_NODE_FOREIGN_ZSET, ip, 999L);
                    log.info("调度IP更改状态后的 timeForeignMap = " + ChannelSupervise.timeForeignMap + " , ipDisPatchForeignQueue = " + redisUtils.zRange(RedisKey.SPIDER_NODE_FOREIGN_ZSET, 0, -1));
                }
                break;
            case 3:
                redisUtils.zAdd(RedisKey.SPIDER_NODE_FOREIGN_ZSET, ip, System.currentTimeMillis() + 1000);
                break;
            case 4:
                redisUtils.zAdd(RedisKey.SPIDER_NODE_FOREIGN_ZSET, ip, System.currentTimeMillis() + 2000);
                break;
            case 5:
                redisUtils.zAdd(RedisKey.SPIDER_NODE_FOREIGN_ZSET, ip, System.currentTimeMillis() + 3000);
                break;
            case 6:
                redisUtils.zRemove(RedisKey.SPIDER_NODE_FOREIGN_ZSET, ip);
                ChannelSupervise.timeForeignMap.remove(ip);
                if (!redisUtils.hasKey(RedisKey.SPIDER_NODE_FOREIGN_ZSET)) {
                    String warnMsg = "国外服务器,调度Ip告警！！！ipDisPatchForeignQueue和timeForeignMap都为NULL,没有足够的爬虫机器进行处理了！！！";
                    dingWarnUtils.sendWarnMsg(warnMsg, "IpDispatchServiceImplForeign");
                    log.error(warnMsg);
                }
                break;

            default:
                break;

        }


        return null;
    }


}
