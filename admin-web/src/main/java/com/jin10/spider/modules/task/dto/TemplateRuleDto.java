package com.jin10.spider.modules.task.dto;

import lombok.Data;

import java.util.List;

/**
 * @author hongda.fang
 * @date 2019-11-18 14:54
 * ----------------------------------------------
 */
@Data
public class TemplateRuleDto {



    /**
     * 模版id
     */
//    private Long tempId;

    /**
     * 内容类型，1文本，2json，3jsonp
     */
    private Integer type;

    /**
     * 编码
     */
    private String headers;

    /**
     * 方法，post、get
     */
    private String method;

    /**
     * 附带参数
     */
    private String params;

    /**
     * 规则，json
     */

    private List<TemplateRuleDto.Rule> rules;






//    /**
//     *  是否为详情页面的规则
//     */
//    private Boolean isDetail;



    @Data
    public static class Rule {

        private String rule;
        private String key;
    }

}
