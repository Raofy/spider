package com.jin10.spider.modules.template.mapper;

import com.jin10.spider.common.utils.Constant;
import com.jin10.spider.modules.template.entity.TemplateRule;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 爬虫模版规则 Mapper 接口
 * </p>
 *
 * @author hongda.fang
 * @since 2019-11-11
 */
@Mapper
public interface TemplateRuleMapper extends BaseMapper<TemplateRule> {

    @Select("SELECT a.* FROM template_rule a LEFT JOIN template b on a.temp_id = b.id WHERE b.`status` = " + Constant.TEMPLATE.STATUS_RUNING)
    List<TemplateRule> findRuningRules();

}
