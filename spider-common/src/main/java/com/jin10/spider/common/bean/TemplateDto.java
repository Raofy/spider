package com.jin10.spider.common.bean;

import lombok.Data;

import java.util.Objects;

/**
 * @author hongda.fang
 * @date 2019-11-12 14:31
 * ----------------------------------------------
 */
@Data
public class TemplateDto {


    private TemplateRuleDto tempRule;

    private TemplateRuleDto detailTempRule;

    private String encoding;

    /**
     * 去重方式 ：1 通过链接，2通过文字
     */
    private Integer filterBy;


    private Long tempId;
    private String channel;
    private String category;
    private Long deptId;
    private String remark;
    private String source;
    private Long spiderTempId;
    private String encryptType;

    /**
     * 顶级域名
     */
    private String domainName;

    /**
     * 代理Ip
     */
    private String proxy;

    /**
     * 代理等级
     */
    private int proxyLevel;

    /**
     * 额外代理字段
     */
    private String proxyExtra;

    /**
     * 代理类型
     */
    private String proxyType;

    /**
     * 代理是否属于国外
     */
    private boolean proxyWheForeign;

    /**
     * 拼接URL随机参数
     */
    private Boolean randomQueryParams;

    /**
     * 超时时间
     */
    private Integer timeout;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TemplateDto)) {
            return false;
        }
        TemplateDto that = (TemplateDto) o;
        return getTempId().equals(that.getTempId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTempId());
    }
}
