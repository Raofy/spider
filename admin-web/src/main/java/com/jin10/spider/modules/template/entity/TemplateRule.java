package com.jin10.spider.modules.template.entity;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 爬虫模版规则
 * </p>
 *
 * @author hongda.fang
 * @since 2019-11-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TemplateRule implements Serializable {


    private static final long serialVersionUID = 4368977978128622500L;

    @TableId
    private Long id;

    /**
     * 模版id
     */
    private Long tempId;

    /**
     * 内容类型，1文本，2json，3jsonp
     */

    @Min(value = 1, message = "tempRule.type 必须为  1、2、3")
    @Max(value = 3 ,message = "tempRule.type 必须为  1、2、3" )
    private Integer type;

    /**
     * 编码
     */
    private String headers;

    /**
     * 方法，post、get
     */
    @NotNull
    private String method;

    /**
     * 附带参数
     */
    private String params;

    /**
     * 规则，json
     */
    @NotNull
    @TableField(exist = false)
    private List<Rule> rules;

    @JsonIgnore
    private String rulesJson;



    /**
     *  是否为详情页面的规则
     */
    @JsonIgnore
    private Boolean isDetail;



    @Data
    public static class Rule {


        /**
         * rule :
         * lable : 标题
         * key : title
         */
        @NotNull
        private String rule;
//        @NotNull
//        private String label;
        @NotNull
        private String key;


    }


    public String rulesBeanToJson(){
        if (rules !=null){
            rulesJson = JSONUtil.toJsonStr(rules);
            return rulesJson;
        }
        rulesJson = null;
        return null;
    }


    public String jsonToBean(){
        if (JSONUtil.isJson(rulesJson)){
            rules = JSONUtil.toList(JSONUtil.parseArray(rulesJson), Rule.class);
        }
        rulesJson = null;
        return null;
    }

}
