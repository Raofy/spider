package com.jin10.spider.modules.statistics.receiver;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.jin10.spider.common.bean.UrlTask;
import com.jin10.spider.common.config.RabbitmqConfig;
import com.jin10.spider.common.constants.ActionCodeConstants;
import com.jin10.spider.common.constants.RedisKey;
import com.jin10.spider.common.utils.RedisUtils;
import com.jin10.spider.modules.statistics.bean.TaskLog;
import com.jin10.spider.modules.statistics.service.IDingTalkWarnService;
import com.jin10.spider.modules.statistics.service.impl.TaskLogServiceImpl;
import com.jin10.spider.modules.template.entity.StopTemplateReason;
import com.jin10.spider.modules.template.entity.Template;
import com.jin10.spider.modules.template.service.impl.TemplateServiceImpl;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.jin10.spider.common.netty.global.ClientChannelGroup.clientGroup;


/**
 * @author hongda.fang
 * @date 2019-11-08 14:37
 * ----------------------------------------------
 * 获取爬虫日志并处理
 * ----------------------------------------------
 */
@Component
@Slf4j
public class TaskLogMqReceiver {

    /**
     * 任务数量计数器
     */
    private AtomicInteger atomCount = new AtomicInteger(0);

    @Autowired
    private ThreadPoolTaskExecutor asyncMq;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private TemplateServiceImpl templateService;
    @Autowired
    private IDingTalkWarnService dingTalkWarnService;
    @Autowired
    private TaskLogServiceImpl taskLogService;


    @RabbitListener(queues = RabbitmqConfig.SPIDER_QUEUE)
    @RabbitHandler
    public void process(String message) {
        asyncMq.submit(() -> dealMsg(message));
    }


    /**
     * 获取 爬虫日志
     *
     * @param message
     */
    private void dealMsg(String message) {

        int activeCount = asyncMq.getActiveCount();
        log.info("线程池活跃线程数 == { " + activeCount + " }个~~~");
        atomCount.incrementAndGet();
        log.info("atomCount数量为 : " + atomCount.get());
        if (!JSONUtil.isJson(message)) {
            log.error("msg 不符合JSON规范,请检查消息格式！！！");
            return;
        }
        try {
            TaskLog taskLogRequest = JSONUtil.toBean(message, TaskLog.class);
            taskLogRequest.setMqTime(new Date());
            log.info(" 开始执行 taskId = " + taskLogRequest.getTaskId() + "任务！！！");

            //处理爬虫日志
            processTaskLog(taskLogRequest);
            log.info(" 爬虫MQ任务 taskId = " + taskLogRequest.getTaskId() + " 执行完毕！");
        } catch (Exception e) {
            log.error("消息处理异常:  ", e);
        }


    }


    /**
     * 处理获取的日志
     */
    public void processTaskLog(TaskLog taskLog) {
        //检测失败任务
        checkFailTask(taskLog);
        taskLog.setOverTime(new Date());
        TaskLog save = taskLogService.save(taskLog);
        //推送到服务器端
        if (CollUtil.isNotEmpty(clientGroup)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("action", ActionCodeConstants.TASK_LOG_MSG);
            jsonObject.put("taskLog", save);
            clientGroup.writeAndFlush(new TextWebSocketFrame(jsonObject.toJSONString()));
        }


    }


    /**
     * 检查任务是否失败
     *
     * @param taskLog
     */
    private void checkFailTask(TaskLog taskLog) {

        if (redisUtils.hasKey(RedisKey.TEMPLATE_SUFFIX + taskLog.getTempId())) {
            Template template = (Template) redisUtils.get(RedisKey.TEMPLATE_SUFFIX + taskLog.getTempId());
            if (ObjectUtil.isNotNull(template)) {
                try {
                    log.info("MQ处理 tempId = " + template.getId() + " taskUuidMap = " + template.getTaskUuidMap());
                    if (template.getTaskUuidMap().containsKey(taskLog.getTaskId())) {
                        template.getConcurrentNum().decrementAndGet();
                        ConcurrentHashMap<String, Long> taskUuidMap = template.getTaskUuidMap();
                        taskUuidMap.remove(taskLog.getTaskId());
                        template.setTaskUuidMap(taskUuidMap);

                        taskLog.setChangeStateTime(new Date());
                        if (!taskLog.wheSuccess()) {
                            if (ObjectUtil.isNull(template.getFailTime())) {
                                template.setFailTime(new Date());
                            }
                            template.getAllowFailTimes().decrementAndGet();
                            if (template.getAllowFailTimes().get() <= 0) {
                                //设置为false
                                template.setPushFlag(false);
                                boolean stop = templateService.stop(template.getId());
                                if (stop) {
                                    dingTalkWarnService.taskFailMarkDownWarn(taskLog, template);
                                    LambdaUpdateWrapper<Template> updateWrapper = new LambdaUpdateWrapper<>();
                                    updateWrapper.eq(Template::getId, taskLog.getTempId()).set(Template::getStopTime, new Date())
                                            .set(Template::getStopReason, StopTemplateReason.TOO_MANY_ERROR_TIMES);
                                    templateService.update(null, updateWrapper);
                                } else {
                                    log.error("停止 " + taskLog.getTempId() + "失败！");
                                    dingTalkWarnService.msgWarn("停止源=" + taskLog.getTempId() + "失败！");
                                }
                            }

                        } else {
                            if (template.getAllowFailTimes().get() < template.getRetryTimes()) {
                                template.setAllowFailTimes(new AtomicInteger(template.getRetryTimes()));
                                template.setFailTime(null);
                            }
                        }
                        redisUtils.set(RedisKey.TEMPLATE_SUFFIX + taskLog.getTempId(), template);
                    } else {
                        log.error("警告！罕见异常！  TaskUuidMap 不包含 taskId , taskUuidMap = " + template.getTaskUuidMap() + " taskLog uuid= " + taskLog.getTaskId());
                    }

                } catch (Exception e) {
                    log.error("template taskUuidMap = " + template.getTaskUuidMap() + " taskLog uuid= " + taskLog.getTaskId());
                    e.printStackTrace();
                }

            }
        } else {
            log.error("警告！！！ 爬虫日志对应的任务模板tempId=" + taskLog.getTempId() + "不存在！！！，请检查redis和数据库模板数据是否一致");
        }


        UrlTask urlTask = (UrlTask) redisUtils.get(RedisKey.URLTASK_PREFIX + taskLog.getTaskId());
        if (ObjectUtil.isNotNull(urlTask)) {
            taskLog.setCreatTime(urlTask.getCreateTime());
            taskLog.setSendS1Time(urlTask.getSendS1Time());
            taskLog.setReceiveS1Time(urlTask.getReceiveS1Time());
            taskLog.setPushTime(urlTask.getPushTime());
            taskLog.setWheForeign(urlTask.isWeForeign());
            String proxy = taskLog.getProxy();
            if (StringUtils.isNotBlank(proxy)) {
                String[] split = proxy.split(":");
                if (split.length == 2) {
                    taskLog.setProxyComplete(proxy);
                    taskLog.setProxy(split[0]);
                    taskLog.setProxyPort(split[1]);
                }
            }
            redisUtils.del(RedisKey.URLTASK_PREFIX + taskLog.getTaskId());
        } else {
            log.error("罕见异常！！！ urlTask不存在 ！ taskId = " + taskLog.getTaskId());
        }

    }


}
