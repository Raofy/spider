package com.jin10.spider.modules.template.service;

import com.jin10.spider.modules.template.entity.TemplateRule;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 爬虫模版规则 服务类
 * </p>
 *
 * @author hongda.fang
 * @since 2019-11-11
 */
public interface ITemplateRuleService extends IService<TemplateRule> {

    int deleteByTempId(Long tempId);

    void updateRule(List<TemplateRule> rules);

    Map<Long, List<TemplateRule>> findRuningRule();

    Map<Long, List<TemplateRule>> inTempIds(List<Long> tempIds);

}
