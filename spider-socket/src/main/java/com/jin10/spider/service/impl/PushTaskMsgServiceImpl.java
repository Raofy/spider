package com.jin10.spider.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jin10.spider.bean.IpDispatch;
import com.jin10.spider.bean.UrlTaskDtoPri;
import com.jin10.spider.cache.GlobalCache;
import com.jin10.spider.common.annotation.ActionCode;
import com.jin10.spider.common.bean.UrlTask;
import com.jin10.spider.common.bean.UrlTaskDto;
import com.jin10.spider.common.constants.ActionCodeConstants;
import com.jin10.spider.common.constants.RedisKey;
import com.jin10.spider.common.enums.MsgCodeEnum;
import com.jin10.spider.common.netty.global.ChannelSupervise;
import com.jin10.spider.common.netty.interf.IActionSocketService;
import com.jin10.spider.common.utils.RedisUtils;
import com.jin10.spider.utils.DingWarnUtils;
import com.jin10.spider.utils.NettyChannelUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Airey
 * @date 2020/4/1 14:45
 * ----------------------------------------------
 * 推送消息到python端
 * ----------------------------------------------
 */
@Service
@ActionCode(value = ActionCodeConstants.TRASFORM_MSG)
@Slf4j
public class PushTaskMsgServiceImpl implements IActionSocketService {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private NettyChannelUtils nettyChannelUtils;

    @Autowired
    private DingWarnUtils dingWarnUtils;

    private Map<String, Integer> map = new ConcurrentHashMap<String, Integer>();


    /**
     * 此处为单线程处理，多线程的时候urlTaskDto要考虑线程安全问题，加锁等操作
     *
     * @param context
     * @param message
     * @return
     */
    @Override
    public Object doAction(ChannelHandlerContext context, String message) {

        try {

            log.info("接收到调度端消息: " + message);
            JSONObject jsonObj = JSONObject.parseObject(message);
            JSONObject taskJson = jsonObj.getJSONObject("task");
            /**
             * 任务对象
             */
            UrlTaskDto urlTaskDto = JSON.toJavaObject(taskJson, UrlTaskDto.class);
            if (redisUtils.hasKey(RedisKey.URLTASK_PREFIX + urlTaskDto.getTaskUuid())) {
                UrlTask urlTask = (UrlTask) redisUtils.get(RedisKey.URLTASK_PREFIX + urlTaskDto.getTaskUuid());
                urlTask.setReceiveS1Time(new Date());
                redisUtils.set(RedisKey.URLTASK_PREFIX + urlTaskDto.getTaskUuid(), urlTask);
            } else {
                log.error("警告！redis 中不存在 " + RedisKey.URLTASK_PREFIX + urlTaskDto.getTaskUuid());
            }

            //推送消息到python端
            if (urlTaskDto.isWeForeign()) {
                boolean pushForeignFlag = pushMsg(urlTaskDto, true);
                if (!pushForeignFlag) {
                    log.error("国外服务器!调度任务分配失败！！！,国外任务 taskId = " + urlTaskDto.getTaskUuid() + " 被暂时分配到缓存中！");
                    GlobalCache.taskDtoPrisForeignQueue.offer(new UrlTaskDtoPri(urlTaskDto, System.currentTimeMillis()));
                }
            } else {
                boolean pushFlag = pushMsg(urlTaskDto);
                if (!pushFlag) {
                    log.error("调度任务分配失败！！！,taskId = " + urlTaskDto.getTaskUuid() + " 被暂时分配到缓存中！");
                    GlobalCache.taskDtoPrisQueue.offer(new UrlTaskDtoPri(urlTaskDto, System.currentTimeMillis()));
                }
            }

            //推送新增任务监控信息
            pushProductTask(urlTaskDto);
            log.info("调度端消息: taskId = " + urlTaskDto.getTaskUuid() + " 处理完毕！！！");
        } catch (Exception e) {
            log.error("处理调度端消息" + message + "异常！！！", e);
        }


        return null;
    }

    /**
     * 推送调度任务消息到python端
     *
     * @param taskMsg
     */
    public boolean pushMsg(UrlTaskDto taskMsg) {

        if (ObjectUtil.isNull(taskMsg)) {
            log.error("调度端消息为NULL,请检查消息格式是否正确！！！");
            return false;
        }

        if (redisUtils.hasKey(RedisKey.SPIDER_NODE_ZSET)) {
            Map<String, Channel> ipChannelMap = ChannelSupervise.actionIpGlobalGroupMap.get(ActionCodeConstants.TASK_PUSH);
            if (CollUtil.isEmpty(ipChannelMap)) {
                String warnMsg = "ipChannelMap为NULL,没有足够的爬虫机器进行处理！！！";
                log.error(warnMsg);
                dingWarnUtils.sendWarnMsg(warnMsg, "ipChannelMap");
                redisUtils.del(RedisKey.SPIDER_NODE_ZSET);
                return false;
            }
            if (CollUtil.isEmpty(ChannelSupervise.timeMap)) {
                String warnMsg = "timeMap为NULL,没有足够的爬虫机器进行处理！！！";
                log.error(warnMsg);
                dingWarnUtils.sendWarnMsg(warnMsg, "timeMap");
                redisUtils.del(RedisKey.SPIDER_NODE_ZSET);
                return false;
            } else {
                log.info("timeMap 当前所在组中的channel为 : " + ChannelSupervise.timeMap.keySet());
                log.info("ipDispatchQueue : " + redisUtils.zRange(RedisKey.SPIDER_NODE_ZSET, 0, -1));
            }
            return writeAndFlushMsg(taskMsg, ipChannelMap);
        } else {
            if (CollUtil.isEmpty(ChannelSupervise.timeMap)) {
                String warnMsg = "ipDispatchQueue为NULL,检查timeMap也为NULL,没有足够的爬虫机器进行处理！！!";
                log.error(warnMsg);
                dingWarnUtils.sendWarnMsg(warnMsg, "ipDispatchQueue");
                return false;
            } else {
                for (String timeIp : ChannelSupervise.timeMap.keySet()) {
                    redisUtils.zAdd(RedisKey.SPIDER_NODE_ZSET, timeIp, 999L);
                }
                pushMsg(taskMsg);
                return true;
            }

        }

    }


    /**
     * 推送单条任务消息
     *
     * @param taskMsg
     * @param ipChannelMap
     */
    private boolean writeAndFlushMsg(UrlTaskDto taskMsg, Map<String, Channel> ipChannelMap) {
        boolean flag = true;
        try {
            String ip;
            Set<Object> ipObj = redisUtils.zRange(RedisKey.SPIDER_NODE_ZSET, 0, 0);
            if (CollUtil.isNotEmpty(ipObj)) {
                ip = (String) ipObj.iterator().next();
            } else {
                log.error("分配ip失败！！！RedisKey.SPIDER_NODE_ZSET 为NULL！");
                String warnMsg = "分配ip失败！！！RedisKey.SPIDER_NODE_ZSET 为NULL!";
                log.error(warnMsg);
                dingWarnUtils.sendWarnMsg(warnMsg, "redisKeyHome");
                return false;
            }
            if (ChannelSupervise.timeMap.containsKey(ip)) {
                Channel channel = ipChannelMap.get(ip);
                if (ObjectUtil.isNotNull(channel)) {
                    channel.writeAndFlush(new TextWebSocketFrame(JSONObject.toJSONString(taskMsg)));
                    redisUtils.zAdd(RedisKey.SPIDER_NODE_ZSET, ip, System.currentTimeMillis());
                    log.info("执行的ip为:  " + ip + ", taskId= " + taskMsg.getTaskUuid());
                    if (map.containsKey(ip)) {
                        Integer anInt = MapUtil.getInt(map, ip);
                        anInt++;
                        map.put(ip, anInt);
                    } else {
                        map.put(ip, 1);
                    }
                    log.info("map == " + map);

                    if (redisUtils.hasKey(RedisKey.URLTASK_PREFIX + taskMsg.getTaskUuid())) {
                        Object obj = redisUtils.get(RedisKey.URLTASK_PREFIX + taskMsg.getTaskUuid());
                        if (obj instanceof UrlTask) {
                            UrlTask urlTask = (UrlTask) obj;
                            urlTask.setPushTime(new Date());
                            redisUtils.set(RedisKey.URLTASK_PREFIX + taskMsg.getTaskUuid(), urlTask);
                        }
                    } else {
                        log.error("警告！！！ redis中找不到urlTask = " + taskMsg.getTaskUuid());
                    }

                } else {
                    log.warn("ip : " + ip + " channel为空！！！");
                    if (redisUtils.hasKey(RedisKey.SPIDER_NODE_ZSET)) {
                        Map<String, Channel> ipChannelMap2 = ChannelSupervise.actionIpGlobalGroupMap.get(ActionCodeConstants.TASK_PUSH);
                        writeAndFlushMsg(taskMsg, ipChannelMap2);
                    } else {
                        flag = false;
                        String warnMsg = "没有足够的爬虫机器进行处理！！！channel为空！";
                        log.error(warnMsg);
                        dingWarnUtils.sendWarnMsg(warnMsg, "warn2");
                    }

                }
            } else {
                log.warn("ip : " + ip + " 已经不在timeMap中！！！ timeMap= " + ChannelSupervise.timeMap);
                redisUtils.zRemove(RedisKey.SPIDER_NODE_ZSET, ip);
                if (redisUtils.hasKey(RedisKey.SPIDER_NODE_ZSET)) {
                    Map<String, Channel> ipChannelMap3 = ChannelSupervise.actionIpGlobalGroupMap.get(ActionCodeConstants.TASK_PUSH);
                    writeAndFlushMsg(taskMsg, ipChannelMap3);
                } else {
                    flag = false;
                    String warnMsg = "没有足够的爬虫机器进行处理！！！ timeMap不包含ip";
                    log.error(warnMsg);
                    dingWarnUtils.sendWarnMsg(warnMsg, "warn1");
                }
            }

        } catch (Exception e) {
            log.error("获取ip异常！！！", e);
            return false;
        }

        return flag;
    }

    /**
     * 推送消息 国外
     *
     * @param taskMsg
     * @param foreign
     */
    public boolean pushMsg(UrlTaskDto taskMsg, boolean foreign) {

        if (ObjectUtil.isNull(taskMsg)) {
            log.error("调度端消息为NULL,请检查消息格式是否正确！！！");
            return false;
        }

        if (redisUtils.hasKey(RedisKey.SPIDER_NODE_FOREIGN_ZSET)) {
            Map<String, Channel> ipChannelMap = ChannelSupervise.actionIpGlobalGroupMap.get(ActionCodeConstants.TASK_PUSH_FOREIGN);
            if (CollUtil.isEmpty(ipChannelMap)) {
                String warnMsg = "国外服务器！！！ipChannelMap为NULL,没有足够的爬虫机器进行处理！！！";
                log.error(warnMsg);
                dingWarnUtils.sendWarnMsg(warnMsg, "ipChannelMapForeign");
                redisUtils.del(RedisKey.SPIDER_NODE_FOREIGN_ZSET);
                return false;
            }
            if (CollUtil.isEmpty(ChannelSupervise.timeForeignMap)) {
                String warnMsg = "国外服务器！！！timeForeignMap为NULL,没有足够的爬虫机器进行处理！！！";
                log.error(warnMsg);
                dingWarnUtils.sendWarnMsg(warnMsg, "timeMapForeign");
                redisUtils.del(RedisKey.SPIDER_NODE_FOREIGN_ZSET);
                return false;
            } else {
                log.info("国外服务器！！！timeForeignMap 当前所在组中的channel为 : " + ChannelSupervise.timeForeignMap.keySet());
                log.info("国外服务器！！！ipForeignDispatchQueue : " + redisUtils.zRange(RedisKey.SPIDER_NODE_FOREIGN_ZSET, 0, -1));
            }
            return writeAndFlushMsg(taskMsg, ipChannelMap, foreign);
        } else {
            if (CollUtil.isEmpty(ChannelSupervise.timeForeignMap)) {
                String warnMsg = "国外服务器！！！ ipForeignDispatchQueue为NULL,检查timeForeignMap也为NULL,没有足够的爬虫机器进行处理！！!";
                log.error(warnMsg);
                dingWarnUtils.sendWarnMsg(warnMsg, "ipForeignDispatchQueue");
                return false;
            } else {
                for (String timeIp : ChannelSupervise.timeForeignMap.keySet()) {
                    redisUtils.zAdd(RedisKey.SPIDER_NODE_FOREIGN_ZSET, timeIp, 999L);
                }
                pushMsg(taskMsg, true);
                return true;
            }
        }


    }


    /**
     * 推送单条任务消息
     *
     * @param taskMsg
     * @param ipChannelMap
     * @param isforeign
     */
    private boolean writeAndFlushMsg(UrlTaskDto taskMsg, Map<String, Channel> ipChannelMap, boolean isforeign) {

        boolean flag = true;
        try {
            String ip;
            Set<Object> foreignIpObj = redisUtils.zRange(RedisKey.SPIDER_NODE_FOREIGN_ZSET, 0, -1);
            if (CollUtil.isNotEmpty(foreignIpObj)) {
                ip = (String) foreignIpObj.iterator().next();
            } else {
                log.error("国外服务器！分配ip失败！！！RedisKey.SPIDER_NODE_FOREIGN_ZSET 为NULL！");
                String warnMsg = "国外服务器！分配ip失败！！！RedisKey.SPIDER_NODE_FOREIGN_ZSET 为NULL!";
                log.error(warnMsg);
                dingWarnUtils.sendWarnMsg(warnMsg, "redisKeyForeign");
                return false;
            }
            log.info("国外服务器！！！执行的ip为:  " + ip);
            if (ChannelSupervise.timeForeignMap.containsKey(ip)) {
                Channel channel = ipChannelMap.get(ip);
                if (ObjectUtil.isNotNull(channel)) {
                    channel.writeAndFlush(new TextWebSocketFrame(JSONObject.toJSONString(taskMsg)));
                    redisUtils.zAdd(RedisKey.SPIDER_NODE_FOREIGN_ZSET, ip, System.currentTimeMillis());
                    log.info("国外服务器！！！ip : " + ip + " : taskId= " + taskMsg.getTaskUuid());
                    if (redisUtils.hasKey(RedisKey.URLTASK_PREFIX + taskMsg.getTaskUuid())) {
                        UrlTask urlTask = (UrlTask) redisUtils.get(RedisKey.URLTASK_PREFIX + taskMsg.getTaskUuid());
                        urlTask.setPushTime(new Date());
                        redisUtils.set(RedisKey.URLTASK_PREFIX + taskMsg.getTaskUuid(), urlTask);
                    }

                } else {
                    log.error("国外服务器！！！ip : " + ip + " channel为空！！！");
                    if (redisUtils.hasKey(RedisKey.SPIDER_NODE_FOREIGN_ZSET)) {
                        Map<String, Channel> ipChannelMap2 = ChannelSupervise.actionIpGlobalGroupMap.get(ActionCodeConstants.TASK_PUSH_FOREIGN);
                        writeAndFlushMsg(taskMsg, ipChannelMap2);
                    } else {
                        flag = false;
                        String warnMsg = "没有足够的爬虫机器进行处理！！！channel为空！";
                        log.error(warnMsg);
                        dingWarnUtils.sendWarnMsg(warnMsg, "warn2Foreign");
                    }
                }
            } else {
                log.error("国外服务器！！！ip : " + ip + " 已经不在timeForeignMap中！！！ timeForeignMap= " + ChannelSupervise.timeForeignMap);
                redisUtils.zRemove(RedisKey.SPIDER_NODE_FOREIGN_ZSET, ip);
                if (redisUtils.hasKey(RedisKey.SPIDER_NODE_FOREIGN_ZSET)) {
                    Map<String, Channel> ipChannelMap3 = ChannelSupervise.actionIpGlobalGroupMap.get(ActionCodeConstants.TASK_PUSH_FOREIGN);
                    writeAndFlushMsg(taskMsg, ipChannelMap3);
                } else {
                    flag = false;
                    String warnMsg = "国外服务器！！！没有足够的爬虫机器进行处理！！！ timeForeignMap不包含ip";
                    log.error(warnMsg);
                    dingWarnUtils.sendWarnMsg(warnMsg, "warn1Foreign");
                }
            }

        } catch (Exception e) {
            log.error("国外服务器！！！获取ip异常！！！", e);
            return false;
        }

        return flag;

    }


    /**
     * 推送队列生产任务
     *
     * @param urlTaskDto
     */
    public void pushProductTask(UrlTaskDto urlTaskDto) {
        if (ObjectUtil.isNotNull(urlTaskDto)) {
            nettyChannelUtils.pushRegisterActionChannel(ActionCodeConstants.TASK_QUEUE_INFO, MsgCodeEnum.TASK_QUEUE_PRODUCT, urlTaskDto);
        }
    }


}
