package com.jin10.spider.modules.template.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jin10.spider.modules.template.entity.TemplateRule;
import com.jin10.spider.modules.template.mapper.TemplateRuleMapper;
import com.jin10.spider.modules.template.service.ITemplateRuleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 爬虫模版规则 服务实现类
 * </p>
 *
 * @author hongda.fang
 * @since 2019-11-11
 */
@Service
public class TemplateRuleServiceImpl extends ServiceImpl<TemplateRuleMapper, TemplateRule> implements ITemplateRuleService {

    @Override
    public int deleteByTempId(Long tempId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("temp_id", tempId);
        baseMapper.delete(queryWrapper);
        return baseMapper.delete(queryWrapper);
    }

    @Override
    public void updateRule(List<TemplateRule> rules) {
        if (CollUtil.isNotEmpty(rules)){
            for (TemplateRule rule : rules){
                deleteByTempId(rule.getTempId());
                rule.rulesBeanToJson();
            }
            saveOrUpdateBatch(rules);
        }
    }

    @Override
    public Map<Long, List<TemplateRule>> findRuningRule() {
        List<TemplateRule> runningRule = baseMapper.findRuningRules();
        if (CollUtil.isNotEmpty(runningRule)){
            for (TemplateRule rule : runningRule){
                rule.jsonToBean();
            }
            Map<Long, List<TemplateRule>> collect = runningRule.stream().collect(Collectors.groupingBy(TemplateRule::getTempId));
            return collect;
        }
        return null;
    }

    @Override
    public Map<Long, List<TemplateRule>>  inTempIds(List<Long> tempIds) {
        QueryWrapper queryWrapper = new QueryWrapper<TemplateRule>();
        queryWrapper.in("temp_id", tempIds);
        List<TemplateRule> list = baseMapper.selectList(queryWrapper);
        Map<Long, List<TemplateRule>> collect = list.stream().collect(Collectors.groupingBy(TemplateRule::getTempId));
        return collect;
    }
}
