package com.jin10.spider;

import cn.hutool.json.JSONUtil;
import com.jin10.spider.common.utils.Constant;
import com.jin10.spider.modules.template.entity.Template;
import com.jin10.spider.modules.template.entity.TemplateRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NewDataTest {


    public static void main(String[] args) {


        System.out.println(JSONUtil.toJsonStr(getRequest()));

    }

    public static Template getRequest(){
        Template template = new Template();

        template.setTitle("国家版权局");
        template.setPageSite("https://www.baidu.com/s?ie=utf-8&f=8&rsv_bp=1&rsv_idx=1&tn=baidu&wd=%E9%A9%AC%E4%BA%91&oq=test&rsv_pq=8520aa260031474b&rsv_t=212ekHo6apcXqbVgCGnO3o%2BEEE1rSZUkas6BDEem%2B2KfEFkMTV%2FxhU%2B1ag4&rqlang=cn&rsv_enter=1&rsv_dl=tb&inputT=963&rsv_sug3=13&rsv_sug1=12&rsv_sug7=100&rsv_sug2=0&rsv_sug4=963");
        template.setChannel("通知公告");
        template.setIntervalTime(new Random().nextInt(10000));
        template.setCategory("aa");
        template.setPriority((int) Math.random() * 100);
        template.setFilterBy(Constant.TEMPLATE.FILTER_BY_LINK);
        template.setEncoding("utf-8");
        template.setDeptId(2L);
        template.setTempRule(getTemplateRule(false));
        template.setDetailTempRule(getTemplateRule(true));

        return template;

    }


    public static TemplateRule getTemplateRule(boolean isDetail){

        TemplateRule templateRule = new TemplateRule();
        templateRule.setType(Constant.TEMPLATE.TYPE_TEXT);
        templateRule.setMethod("get");
        templateRule.setIsDetail(isDetail);
        templateRule.setRules(getRules());

        return templateRule;
    }


    public static List<TemplateRule.Rule> getRules(){
        TemplateRule.Rule rule = new TemplateRule.Rule();
        rule.setKey("title");
//        rule.setLabel("标题");
        rule.setRule("//h3/a");

        List<TemplateRule.Rule> ruleList = new ArrayList<>();



        ruleList.add(rule);

        return ruleList;
    }


}
