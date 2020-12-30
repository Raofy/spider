package com.jin10.spider.modules.task.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.jin10.spider.common.bean.TemplateDto;
import com.jin10.spider.common.bean.UrlTask;
import com.jin10.spider.common.constants.ActionCodeConstants;
import com.jin10.spider.common.utils.Constants;
import com.jin10.spider.common.utils.JsonUtil;
import com.jin10.spider.common.constants.RedisKey;
import com.jin10.spider.common.utils.RedisUtils;
import com.jin10.spider.modules.task.bean.ProxyTemplate;
import com.jin10.spider.modules.task.dto.UrlTaskDto;
import com.jin10.spider.modules.task.handler.SelectIpHandler;
import com.jin10.spider.modules.task.service.IIpProxyProcessService;
import com.jin10.spider.modules.task.service.IProductTaskService;
import com.jin10.spider.modules.template.entity.Template;
import com.jin10.spider.modules.template.entity.TemplateRule;
import com.jin10.spider.modules.template.service.ITemplateRuleService;
import com.jin10.spider.modules.template.service.ITemplateService;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.jin10.spider.common.netty.global.ClientChannelGroup.clientGroup;

/**
 * @author Airey
 * @date 2020/1/15 14:37
 * ----------------------------------------------
 * 生产任务逻辑处理类
 * ----------------------------------------------
 */
@Service
@Slf4j
public class ProductTaskServiceImpl implements IProductTaskService {

    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private ITemplateService templateService;
    @Autowired
    private ITemplateRuleService ruleService;
    @Autowired
    private IIpProxyProcessService proxyProcessService;
    @Autowired
    private SelectIpHandler selectIpHandler;

    private Integer count = 0;


    /**
     * 初始化待运行的任务模板，加载到redis中
     */
    @Override
    public void initRunningTemplate() {
        log.info("====================开始初始化模板！！！");
        initRedisKey();
        List<Template> templateList = templateService.findByStatusRunning();
        /**
         * 对重试失败次数大于5的重新赋值
         */
        templateList.stream().filter(item -> item.getRetryTimes() > 15).forEach(item -> item.setAllowFailTimes(new AtomicInteger(item.getRetryTimes())));
        /**
         * 计算域名对应的个数，数据结构为map(ip地址，个数)
         */
        Map<String, Long> domainMap = templateList.stream().collect(Collectors.groupingBy(Template::getDomainName, Collectors.counting()));
        domainMap.forEach((k, v) -> redisUtils.hset(RedisKey.DOMAIN_NAME_MAP, k, v));
        /**
         * 需要代理的域名
         */
        Map<String, ProxyTemplate> proxyDomainMap = templateList.stream().filter(Template::WheProxy).collect(Collectors.toMap(Template::getDomainName, item -> new ProxyTemplate(item.getProxyLevel(), item.getWeforeign()), (k1, k2) -> k1));
        proxyDomainMap.forEach((k, v) -> redisUtils.hset(RedisKey.DOMAIN_PROXY_MAP, k, v));  // 键 项 值 （domainNameMap，127.0.0.1, 28）

        Map<Long, List<TemplateRule>> runningRule = ruleService.findRuningRule();
        if (!CollectionUtils.isEmpty(runningRule) && !CollectionUtils.isEmpty(templateList)) {
            int part = 1;
            int i = 1;
            int size = templateList.size() / 10;
            for (Template item : templateList) {
                initTemplateRunningRule(runningRule, item);
                if (i < part * size) {
                    item.setPart(part);
                } else if (i == part * size) {
                    item.setPart(part);
                    part++;
                }
                redisUtils.set(getTempIdRedisKey(item.getId()), item);
                redisUtils.hset(RedisKey.TEMPIDMAP, item.getId().toString(), item.getId());
                i++;
            }
        }
        log.info("Redis key tempIdMap size :" + redisUtils.hmget(RedisKey.TEMPIDMAP).size());
    }


    /**
     * 初始化redis，清空所有key
     */
    private void initRedisKey() {
        Set<String> keys = redisUtils.keys("");
        redisUtils.delKeys(keys);
    }


    /**
     * 重新加载模板
     */
    @Override
    public void reloadRunningTemplate() {
        log.info("====================开始重新加载模板！！！");
        redisUtils.del(RedisKey.TEMPIDMAP);
        redisUtils.clearKeys(RedisKey.URLTASK_PREFIX);
        List<Template> templateList = templateService.findByStatusRunning();
        templateList.stream().filter(item -> item.getRetryTimes() > 5).forEach(item -> item.setAllowFailTimes(new AtomicInteger(item.getRetryTimes())));
        Map<Long, List<TemplateRule>> runningRule = ruleService.findRuningRule();
        if (!CollectionUtils.isEmpty(runningRule) && !CollectionUtils.isEmpty(templateList)) {
            templateList.forEach(
                    item -> {
                        initTemplateRunningRule(runningRule, item);
                        if (redisUtils.hasKey(getTempIdRedisKey(item.getId()))) {
                            Object o = redisUtils.get(getTempIdRedisKey(item.getId()));
                            if (o instanceof Template) {
                                Template old = (Template) o;
                                item.setPreTime(old.getPreTime());
                                item.setPushNum(old.getPushNum());
                                item.setAllowFailTimes(old.getAllowFailTimes());
                            }
                        }
                        redisUtils.set(getTempIdRedisKey(item.getId()), item);
                        redisUtils.hset(RedisKey.TEMPIDMAP, item.getId().toString(), item.getId());
                    }
            );
        }
        Map<String, ProxyTemplate> proxyDomainMap = templateList.stream().filter(Template::WheProxy).collect(Collectors.toMap(Template::getDomainName, item -> new ProxyTemplate(item.getProxyLevel(), item.getWeforeign()), (k1, k2) -> k1));
        selectIpHandler.refreshProxyDomainName(proxyDomainMap);
        log.info("Redis key tempIdMap :" + redisUtils.hmget(RedisKey.TEMPIDMAP).size());
    }


    /**
     * 初始化模板页面规则
     *
     * @param runningRule
     * @param item
     */
    private void initTemplateRunningRule(Map<Long, List<TemplateRule>> runningRule, Template item) {
        List<TemplateRule> templateRules = runningRule.get(item.getId());
        if (!CollectionUtils.isEmpty(templateRules)) {
            for (TemplateRule rule : templateRules) {
                if (rule.getIsDetail()) {
                    item.setDetailTempRule(rule);
                } else {
                    item.setTempRule(rule);
                }
            }
        }
    }


    /**
     * 产生待向队列中添加的任务列表
     *
     * @return
     */
    @Override
    public List<UrlTaskDto> createTask() {
        Map<Object, Object> tempIdMap = redisUtils.hmget(RedisKey.TEMPIDMAP);
        if (CollUtil.isNotEmpty(tempIdMap)) {
            List<UrlTaskDto> tasks = new ArrayList<>();
            long curTime = System.currentTimeMillis();
            tempIdMap.forEach((k, v) -> {
                Object o = redisUtils.get(getTempIdRedisKey(k));
                if (o instanceof Template) {
                    Template template = (Template) o;
                    if (ifPassPush(template, curTime)) {
                        UrlTask singleTask = createSingleTask(template);
                        redisUtils.set(getTempIdRedisKey(k), template);
                        redisUtils.set(RedisKey.URLTASK_PREFIX + singleTask.getTaskUuid(), singleTask);
                        log.info("产生 taskId = { " + singleTask.getTaskUuid() + " } 任务!!!");
                        UrlTaskDto urlTaskDto = toUrlTaskDto(template, singleTask);
                        tasks.add(urlTaskDto);
                    }
                }
            });
            //分配合适的Ip
            proxyProcessService.takeProxyIp(tasks);
            return tasks;
        }
        return null;
    }


    /**
     * 判断是否可以推送
     *
     * @param template
     * @param curTime
     * @return
     */
    public boolean ifNeedPush(Template template, long curTime) {

        if (template.isPushFlag()) {
            if (template.getPart() != -1) {
                long preTime = curTime + (template.getPart() - 1 - 8) * 1000;
                template.setPreTime(preTime);
                template.setPart(-1);
                redisUtils.set(getTempIdRedisKey(template.getId()), template);
            } else {
                if (curTime - template.getPreTime() >= template.getIntervalTime() * 1000) {
                    template.setPushNum(template.getPushNum() + 1);
                    template.setPreTime(curTime);
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 是否可以推送,并发数量控制版本
     *
     * @param template
     * @param curTime
     * @return
     */
    private boolean ifPassPush(Template template, long curTime) {

        if (template.isPushFlag()) {
            if (template.getConcurrentNum().get() < 3) {
                if (template.getPart() != -1) {
                    long preTime = curTime + (template.getPart() - 1 - 8) * 1000;
                    template.setPreTime(preTime);
                    template.setPart(-1);
                    redisUtils.set(getTempIdRedisKey(template.getId()), template);
                } else {
                    if (curTime - template.getPreTime() >= template.getIntervalTime() * 1000) {
                        template.setPushNum(template.getPushNum() + 1);
                        template.setPreTime(curTime);
                        template.getConcurrentNum().incrementAndGet();
                        return true;
                    }
                }
            }

        }


        return false;
    }


    /**
     * 获取tempId存储在redis中的key
     *
     * @param tempId
     * @return
     */
    private String getTempIdRedisKey(Object tempId) {
        return RedisKey.TEMPLATE_SUFFIX + tempId;
    }

    /**
     * 产生单个任务
     *
     * @param template
     * @return
     */
    public UrlTask createSingleTask(Template template) {
        UrlTask task = new UrlTask();
        task.setTaskUuid(IdUtil.simpleUUID());
        ConcurrentHashMap<String, Long> taskUuidMap = template.getTaskUuidMap();
        taskUuidMap.put(task.getTaskUuid(), System.currentTimeMillis());
        log.info("TempId = " + template.getId() + " taskUuidMap = " + taskUuidMap);
        template.setTaskUuidMap(taskUuidMap);
        task.setUrl(StringUtils.isNotEmpty(template.getRealSite()) ? template.getRealSite() : template.getPageSite());
        task.setTempId(template.getId());
        task.setCreateTime(new Date());
        task.setWeForeign(template.getWeforeign());
        task.setPriority(template.getPriority());
        return task;
    }

    /**
     * 开始生产调度任务
     */
    @Override
    public void startProductTask() {
        try {
            long startTime = System.currentTimeMillis();
            log.info("开始执行调度器分配的调度任务！！！" + count);
            if (CollUtil.isEmpty(clientGroup)) {
                log.error("socket服务器链接不存在！！！");
                return;
            }
            List<UrlTaskDto> urlTaskDtoList = createTask();
            if (CollectionUtils.isEmpty(urlTaskDtoList)) {
                log.info("urlTaskDtoList 为 null");
                return;
            } else {
                log.info("urlTaskDtoList Size " + urlTaskDtoList.size());
                if (CollUtil.isNotEmpty(urlTaskDtoList)) {
                    JSONObject taskJson = new JSONObject();
                    taskJson.put("action", ActionCodeConstants.TRASFORM_MSG);
                    for (UrlTaskDto item : urlTaskDtoList) {
                        taskJson.put("task", item);
                        String taskString = taskJson.toJSONString();
                        clientGroup.writeAndFlush(new TextWebSocketFrame(taskString));
                        UrlTask urlTask = (UrlTask) redisUtils.get(RedisKey.URLTASK_PREFIX + item.getTaskUuid());
                        urlTask.setSendS1Time(new Date());
                        redisUtils.set(RedisKey.URLTASK_PREFIX + item.getTaskUuid(), urlTask);
                        log.info("发送到socket ==> taskId : ( " + item.getTaskUuid() + " )");
                        count++;
                    }
                }
            }
            long endTime = System.currentTimeMillis();
            log.info("执行时间为 ====》 " + (endTime - startTime) + "ms");
        } catch (Exception e) {
            log.error("startRunCreatTask ====" + e);
        }
    }


    @Override
    public UrlTaskDto toUrlTaskDto(Template template, UrlTask urlTask) {
        if (ObjectUtil.isNotNull(template) && ObjectUtil.isNotNull(urlTask)) {
            UrlTaskDto dto = new UrlTaskDto();
            dto.setCurTemp(null);
            BeanUtil.copyProperties(urlTask, dto);
            TemplateDto templateDto = new TemplateDto();
            BeanUtil.copyProperties(template, templateDto);
            templateDto.setSource(template.getTitle());
            templateDto.setTempId(template.getId());
            if (StringUtils.isEmpty(template.getRealSite())) {
                dto.setUrl(template.getPageSite());
            }
            dto.setTemp(templateDto);
            return dto;
        } else {
            log.error(Constants.LOGGER_STRING + "任务获取模版未能找到对应 template" + JsonUtil.toJson(urlTask));
        }
        return null;
    }


}
