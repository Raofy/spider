package com.jin10.spider.modules.template.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jin10.spider.common.bean.UrlTask;
import com.jin10.spider.common.constants.RedisKey;
import com.jin10.spider.common.exception.BaseException;
import com.jin10.spider.common.utils.*;
import com.jin10.spider.modules.statistics.dto.TaskQueueInfoDto;
import com.jin10.spider.modules.statistics.service.IDingTalkWarnService;
import com.jin10.spider.modules.task.dto.UrlTaskDto;
import com.jin10.spider.modules.task.service.IProductTaskService;
import com.jin10.spider.modules.task.service.UrlTaskManger;
import com.jin10.spider.modules.task.service.UrlTaskProcessService;
import com.jin10.spider.modules.template.dto.TaskQueueInfo;
import com.jin10.spider.modules.template.dto.TestTempDto;
import com.jin10.spider.modules.template.entity.StopTemplateReason;
import com.jin10.spider.modules.template.entity.Template;
import com.jin10.spider.modules.template.entity.TemplateRule;
import com.jin10.spider.modules.template.mapper.TemplateMapper;
import com.jin10.spider.modules.template.request.RenewalRequest;
import com.jin10.spider.modules.template.request.TemplatePageRequest;
import com.jin10.spider.modules.template.service.ITemplateRuleService;
import com.jin10.spider.modules.template.service.ITemplateService;
import com.jin10.spider.modules.template.service.SpiderClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * <p>
 * 爬虫模版 服务实现类
 * </p>
 *
 * @author hongda.fang
 * @since 2019-10-29
 */
@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class TemplateServiceImpl extends ServiceImpl<TemplateMapper, Template> implements ITemplateService {


    @Autowired
    private ITemplateRuleService templateRuleService;
    @Autowired
    private UrlTaskManger urlTaskManger;
    @Autowired
    private ITemplateRuleService ruleService;
    @Autowired
    private UrlTaskProcessService taskProcessService;
    @Autowired
    private SpiderClient spiderClient;
    @Autowired
    private IProductTaskService productTaskService;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private IDingTalkWarnService dingTalkWarnService;

    @Override
    public PageUtils queryPage(TemplatePageRequest params) {

        params.setOrder(Constants.DESC);
        params.setOrderField("id");

        LambdaQueryWrapper<Template> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(params.getStatus() != null, Template::getStatus, params.getStatus())
                .eq(ObjectUtil.isNotNull(params.getWeforeign()), Template::getWeforeign, params.getWeforeign())
                .eq(ObjectUtil.isNotNull(params.getProxyLevel()), Template::getProxyLevel, params.getProxyLevel())
                .eq(ObjectUtil.isNotNull(params.getTempId()), Template::getId, params.getTempId())
                .like(StringUtils.isNotBlank(params.getPageSite()), Template::getPageSite, params.getPageSite())
                .like(StringUtils.isNotBlank(params.getTitle()), Template::getTitle, params.getTitle())
                .like(StringUtils.isNotBlank(params.getChannel()), Template::getChannel, params.getChannel())
                .like(StringUtils.isNotBlank(params.getCategory()), Template::getCategory, params.getCategory())
                .le(params.getExpireTime() != null, Template::getExpireTime, params.getExpireTime());


        if (StringUtils.isNotBlank(params.getMaintainer())) {
            queryWrapper.in(Template::getId, getMaintainerList(params.getMaintainer()));
        }

        IPage<Template> page = this.page(
                new QueryPage<Template>().getPage(params),
                queryWrapper
        );

        List<Template> records = page.getRecords();
        if (params.isShowDetail()) {
            setTempDetail(records);
        }
        return new PageUtils(page);
    }

    /**
     * 获取维护人员对应的模板列表
     *
     * @param maintainer
     * @return
     */
    private Set<Long> getMaintainerList(String maintainer) {
        Object maintainerSet = redisUtils.hget(RedisKey.MAINTAINER_MAP, maintainer);
        if (ObjectUtil.isNotNull(maintainerSet) && maintainerSet instanceof Set) {
            Set<Long> tempIdSet = (Set<Long>) maintainerSet;
            return tempIdSet;
        } else {
            throw new BaseException("维护人员不存在！！！");
        }

    }


    private void setTempDetail(List<Template> records) {
        List<Long> tempIds = records.stream().map(Template::getId).collect(Collectors.toList());
        if (CollUtil.isEmpty(tempIds)) {
            return;
        }
        Map<Long, List<TemplateRule>> longListMap = ruleService.inTempIds(tempIds);
        records.forEach(template -> {
            if (CollUtil.isNotEmpty(longListMap)) {
                List<TemplateRule> rules = longListMap.get(template.getId());

                if (rules != null) {
                    for (TemplateRule rule : rules) {
                        rule.jsonToBean();
                        if (rule.getIsDetail()) {
                            template.setDetailTempRule(rule);
                        } else {
                            template.setTempRule(rule);
                        }
                    }
                }
            }
        });
    }

    @Override
    public List<Template> findByStatusRunning() {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("status", Constant.TEMPLATE.STATUS_RUNING);
        queryWrapper.orderByDesc("priority");

        List<Template> list = baseMapper.selectList(queryWrapper);
        return list;
    }


    @Override
    public Template saveTemp(Template template) {
        validTemp(template);
        TemplateRule tempRule = template.getTempRule();
        TemplateRule detailTempRule = template.getDetailTempRule();
        if (template.getIntervalTime() == null) {
            template.setIntervalTime(10);
        }
        if (StringUtils.isNotBlank(template.getPageSite()) && ObjectUtil.isNull(template.getId())) {
            dealDomainName(template);
        }
        saveOrUpdate(template);
        List<TemplateRule> rules = new ArrayList<>();
        if (tempRule != null) {
            if (StringUtils.isNotBlank(tempRule.getMethod())) {
                tempRule.setMethod(tempRule.getMethod().toUpperCase());
            }
            tempRule.setTempId(template.getId());
            rules.add(tempRule);
        }
        if (detailTempRule != null) {
            detailTempRule.setTempId(template.getId());
            detailTempRule.setIsDetail(true);
            if (StringUtils.isNotBlank(detailTempRule.getMethod())) {
                detailTempRule.setMethod(detailTempRule.getMethod().toUpperCase());
            }
            rules.add(detailTempRule);
        }
        templateRuleService.updateRule(rules);
        initTemplate();
        template = findById(template.getId(), true);
        return template;
    }

    /**
     * 更新备注
     *
     * @param id
     * @param remark
     * @return
     */
    @Override
    public boolean updateRemark(Long id, String remark) {

        LambdaUpdateWrapper<Template> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Template::getId, id).set(Template::getRemark, remark);
        boolean update = this.update(null, updateWrapper);
        return update;
    }

    /**
     * 顶级域名处理
     */
    private void dealDomainName(Template template) {

        try {
            String domainName = DomainNameUtils.getDomainName(template.getPageSite());
            if (StringUtils.isBlank(domainName)) {
                throw new BaseException("顶级域名处理异常！！！");
            }
            if ("ccdi.gov.cn".equals(domainName) || template.getTitle().contains("中纪委")) {
                String msg = "中纪委网站为高危风险网站,系统已强制禁止添加！！！";
                dingTalkWarnService.msgWarn(msg);
                throw new BaseException("严禁添加中纪委相关网站,系统已经强制禁止！！！");
            }
            template.setDomainName(domainName);
            Integer hgetCount = (Integer) redisUtils.hget(RedisKey.DOMAIN_NAME_MAP, domainName);
            if (ObjectUtil.isNotNull(hgetCount)) {
                if (hgetCount >= Constant.DOMAIN_NAME_COUNT) {
                    String msg = "顶级域名 " + domainName + " 数量已经超过" + Constant.DOMAIN_NAME_COUNT + "个, 目前总数量 " + hgetCount + " 个！请注意操作！！！";
                    dingTalkWarnService.msgWarn(msg);
                }
                redisUtils.hincr(RedisKey.DOMAIN_NAME_MAP, domainName, 1);
            } else {
                redisUtils.hset(RedisKey.DOMAIN_NAME_MAP, domainName, 1);
            }

        } catch (MalformedURLException e) {
            log.error("获取顶级域名 " + template.getPageSite() + " 错误！！！", e);
            throw new BaseException("顶级域名处理异常！！！");
        }


    }


    /**
     * 验证模版
     *
     * @param template
     */
    private void validTemp(Template template) {
        List<Template> templates = findByCategoryAndTitleAndChannel(template.getCategory(), template.getTitle(), template.getChannel());
        if (CollUtil.isNotEmpty(templates)) {
            if (template.getId() != null) {
                /**
                 * 用于修改
                 */
                boolean pass = true;
                for (Template findTemp : templates) {
                    pass = template.getId().equals(findTemp.getId());
                    if (!pass) {
                        break;
                    }
                }
                if (!pass) {
                    throw new BaseException("存在category、title、channel 相同的模版" + JsonUtil.toJson(templates));
                }
            } else {
                throw new BaseException("存在category、title、channel 相同的模版" + JsonUtil.toJson(templates));
            }
        }

        TemplateRule tempRule = template.getTempRule();
        TemplateRule detailTempRule = template.getDetailTempRule();
        validRuleKey(tempRule);
        validRuleKey(detailTempRule);
    }

    private void validRuleKey(TemplateRule tempRule) {
        if (tempRule != null && CollUtil.isNotEmpty(tempRule.getRules())) {
            List<TemplateRule.Rule> rules = tempRule.getRules();
            Set<String> keySet = new HashSet<>();
            for (TemplateRule.Rule rule : rules) {
                String key = rule.getKey();
                if ("url".equals(key) || "title".equals(key) || "content".equals(key) || "container".equals(key)) {
                    boolean add = keySet.add(key);
                    if (!add) {
                        throw new BaseException("rules.key 不能重复，" + key);
                    }
                } else {
                    throw new BaseException("存在rules.key 必须为 url、title、content、container");
                }
            }
        }
    }

    @Override
    public boolean stop(Long id) {
        redisUtils.hdel(RedisKey.TEMPIDMAP, String.valueOf(id));
        int i = baseMapper.updateStatusById(id, Constant.TEMPLATE.STATUS_STOP);
//        initTemplate();
        return i > 0 ? true : false;
    }

    @Override
    public boolean start(Long id) {
        int i = baseMapper.updateStatusById(id, Constant.TEMPLATE.STATUS_RUNING);
        if (redisUtils.hasKey(RedisKey.TEMPLATE_SUFFIX + id)) {
            Template template = (Template) redisUtils.get(RedisKey.TEMPLATE_SUFFIX + id);
            Template data = baseMapper.selectById(id);
            if (data.getRetryTimes() > 5) {
                template.setAllowFailTimes(new AtomicInteger(data.getRetryTimes()));
            }
            redisUtils.set(RedisKey.TEMPLATE_SUFFIX + id, template);
        }
        initTemplate();
        return i > 0;
    }

    @Override
    public boolean startBatch(List<Long> idList) {
        Collection<Template> templateList = this.listByIds(idList);
        templateList.forEach(item -> item.setStatus(Constant.TEMPLATE.STATUS_RUNING));
        boolean b = this.updateBatchById(templateList);
        if (b) {
            initTemplate();
        }
        return b;
    }

    /**
     * 模板 续期
     *
     * @param renewalRequest
     * @return
     */
    @Override
    public boolean renewal(RenewalRequest renewalRequest) {

        LambdaUpdateWrapper<Template> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Template::getId, renewalRequest.getTempId()).set(Template::getStatus, Constant.TEMPLATE.STATUS_RUNING)
                .set(Template::getExpireTime, DateUtil.date(renewalRequest.getExpireTime()));
        boolean update = this.update(null, updateWrapper);
        if (update) {
            initTemplate();
        }
        return update;
    }

    @Override
    public boolean delete(Long id) {
        int i = baseMapper.deleteById(id);
        initTemplate();
        return i > 0 ? true : false;
    }

    @Override
    public boolean deletes(List<Long> ids) {
        int i = baseMapper.deleteBatchIds(ids);
        initTemplate();
        return i > 0 ? true : false;
    }

    @Override
    public void initTemplate() {
        productTaskService.reloadRunningTemplate();
    }

    @Override
    public Template findById(Long id, boolean addDetail) {
        Template byId = baseMapper.selectById(id);
        if (byId != null) {
            List<Template> templates = new ArrayList<>();
            templates.add(byId);
            if (addDetail) {
                setTempDetail(templates);

            }
        }
        return byId;
    }


    @Override
    public List<TaskQueueInfo> taskQueueInfos(boolean showAll, Long tempId) {
        List<TaskQueueInfo> queueInfos = new ArrayList<>();
        List<UrlTask> queueList = taskProcessService.getQueueList();
        List<UrlTask> queueForeignList = taskProcessService.getQueueForeignList();
        TaskQueueInfo taskQueueInfo = new TaskQueueInfo();
        taskQueueInfo.setValue(queueList, tempId, showAll);

        TaskQueueInfo taskForeignQueueInfo = new TaskQueueInfo();
        taskForeignQueueInfo.setValue(queueForeignList, tempId, showAll);

        queueInfos.add(taskQueueInfo);
        queueInfos.add(taskForeignQueueInfo);

        return queueInfos;
    }

    @Override
    public List<Template> findByCategoryAndTitleAndChannel(String category, String title, String channel) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("category", category);
        queryWrapper.eq("title", title);
        queryWrapper.eq("channel", channel);
        List<Template> list = baseMapper.selectList(queryWrapper);
        return list;
    }

    @Override
    public List<TaskQueueInfoDto> getTaskQueueInfos() {
        List<TaskQueueInfoDto> taskQueues = new ArrayList<>();
        taskQueues.add(taskProcessService.getQueueInfo(false));
        taskQueues.add(taskProcessService.getQueueInfo(true));
        return taskQueues;
    }

    @Override
    public TestTempDto testTemp(Template temp) {
        if (temp == null) {
            throw new BaseException("没有找对应模版");
        }
        validTemp(temp);
        UrlTaskDto dto = urlTaskManger.convertTestTemp(temp);
        log.warn("testTemp :" + JsonUtils2.writeValue(dto));
        TestTempDto tempDto = spiderClient.requestTestTemp(dto, temp.getWeforeign());
        return tempDto;
    }

    @Override
    public int updateStatusExpireToStop() {
        return baseMapper.updateStatusExpireToStop();
    }

    @Override
    public int updateDeleteNameById(Long id, String user) {
        if (StringUtils.isNotEmpty(user)) {
            log.warn("  updateDeleteNameById  === id:{}  user:{}", id, user);
            return baseMapper.updateDeleteNameById(id, user);
        }

        return 0;
    }


}
