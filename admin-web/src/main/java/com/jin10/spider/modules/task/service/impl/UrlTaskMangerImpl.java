package com.jin10.spider.modules.task.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.jin10.spider.common.bean.TemplateDto;
import com.jin10.spider.common.bean.UrlTask;
import com.jin10.spider.common.constants.RedisKey;
import com.jin10.spider.common.utils.Constants;
import com.jin10.spider.common.utils.JsonUtil;
import com.jin10.spider.common.utils.RedisUtils;
import com.jin10.spider.modules.task.dto.UrlTaskDto;
import com.jin10.spider.modules.task.entity.IpInfo;
import com.jin10.spider.modules.task.handler.SelectIpHandler;
import com.jin10.spider.modules.task.service.IIpProxyProcessService;
import com.jin10.spider.modules.task.service.UrlTaskManger;
import com.jin10.spider.modules.template.entity.Template;
import com.jin10.spider.modules.template.entity.TemplateRule;
import com.jin10.spider.modules.template.service.ITemplateRuleService;
import com.jin10.spider.modules.template.service.ITemplateService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class UrlTaskMangerImpl implements UrlTaskManger {

    @Autowired
    private ITemplateService templateService;
    @Autowired
    private ITemplateRuleService ruleService;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private IpProxyProcessServiceImpl ipProxyProcessService;

    private List<Template> templates;

    private Map<Long, Template> curTempMap;

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 初始化模板
     *
     * @return
     */
    @Override
    public List<Template> initTemplate() {
        if (templates == null) {
            templates = templateService.findByStatusRunning();
        } else {
            Map<Long, Template> mapTemp = templates.stream().collect(Collectors.toMap(Template::getId, Function.identity(), (key1, key2) -> key2));
            List<Template> curTemplates = templateService.findByStatusRunning();
            if (CollUtil.isNotEmpty(curTemplates)) {
                for (Template template : curTemplates) {
                    Template preTemp = mapTemp.get(template.getId());
                    if (preTemp != null) {
                        template.setPreTime(preTemp.getPreTime());
//                        template.setStartTime(preTemp.getStartTime());
                        template.setPushNum(preTemp.getPushNum());
                    }
                }
                templates = curTemplates;
            }
        }
        //将模板加载到内存中
        setCurTempMap();
        //初始化模板代理ip池
//        selectIpHandler.refreshTemp(templates);
        return templates;
    }

    private void setCurTempMap() {
        curTempMap = new HashMap<>();
        if (templates != null) {
            Map<Long, List<TemplateRule>> runningRule = ruleService.findRuningRule();
            if (runningRule != null) {
                for (Template template : templates) {
                    template.setTempRule(null);
                    template.setDetailTempRule(null);
                    List<TemplateRule> rules = runningRule.get(template.getId());
                    if (rules != null) {
                        for (TemplateRule rule : rules) {
                            if (rule.getIsDetail()) {
                                template.setDetailTempRule(rule);
                            } else {
                                template.setTempRule(rule);
                            }
                        }
                    }
                }
            }
            curTempMap = templates.stream().collect(Collectors.toMap(Template::getId, Function.identity(), (key1, key2) -> key2));
        }
    }


    @Override
    public List<UrlTask> creatTask() {
        if (CollUtil.isNotEmpty(templates)) {
            List<UrlTask> tasks = new ArrayList<>();
            long curTime = System.currentTimeMillis();
            for (Template template : templates) {
//                if (template.isNeedPush(curTime)) {
//                    UrlTask task = creatTask(template);
//                    tasks.add(task);
//                }
            }
            return tasks;
        }
        return null;
    }

    @Override
    public UrlTask creatTask(Template template) {
        UrlTask task = new UrlTask();
        task.setTaskUuid(IdUtil.simpleUUID());
        task.setUrl(StringUtils.isNotEmpty(template.getRealSite()) ? template.getRealSite() : template.getPageSite());
        task.setTempId(template.getId());
        task.setCreateTime(new Date());
        task.setWeForeign(template.getWeforeign());
        task.setPriority(template.getPriority());
        return task;
    }


    @Override
    public UrlTaskDto convertAddTemp(UrlTask urlTask) {

        if (urlTask != null && urlTask.getTempId() != null && curTempMap != null) {
            Template template = findTempByCurMap(urlTask);
            if (template != null) {
                return toUrlTaskDto(template, urlTask);
            }
        }
        logger.error(Constants.LOGGER_STRING + "任务获取模版未能找到对应 template" + JsonUtil.toJson(urlTask));
        return null;
    }


    private UrlTaskDto toUrlTaskDto(Template template, UrlTask urlTask) {
        if (template != null) {
            UrlTaskDto dto = new UrlTaskDto();
            dto.setCurTemp(template);
            BeanUtil.copyProperties(urlTask, dto);
            TemplateDto templateDto = new TemplateDto();
            BeanUtil.copyProperties(template, templateDto);
            templateDto.setTempId(template.getId());
            templateDto.setSource(template.getTitle());


            if (template.WheProxy() && !template.getWeforeign()) {
                String proxyIp = "";
                Set<Object> highSet = redisUtils.zRange(RedisKey.ACTIVEIP_ZSET_HIGH, 0, 0);
                if (CollUtil.isNotEmpty(highSet)) {
                    proxyIp = (String) highSet.iterator().next();
                    redisUtils.zAdd(RedisKey.ACTIVEIP_ZSET_HIGH, proxyIp, System.currentTimeMillis());
                }
                if (StringUtils.isNotBlank(proxyIp)) {
                    ipProxyProcessService.setProxyInfo(templateDto, proxyIp);
                } else {
                    logger.error("没有找到合适的Ip,请检查代理池是否正常！！！");
                }
            }

            if (StringUtils.isEmpty(template.getRealSite())) {
                dto.setUrl(template.getPageSite());
            }
            dto.setTemp(templateDto);
            return dto;
        }
        logger.error(Constants.LOGGER_STRING + "任务获取模版未能找到对应 template" + JsonUtil.toJson(urlTask));
        return null;
    }

    @Override
    public UrlTaskDto convertTestTemp(Template template) {
        UrlTask task = creatTask(template);
        UrlTaskDto dto = toUrlTaskDto(template, task);
        return dto;
    }


    @Override
    public Template findTempByCurMap(UrlTask urlTask) {
        if (urlTask != null) {
            Template template = curTempMap.get(urlTask.getTempId());
            if (template == null) {
                template = templateService.findById(urlTask.getTempId(), true);
                if (template != null) {
                    curTempMap.put(template.getId(), template);
                }
            }
            return template;
        }
        return null;
    }


}
