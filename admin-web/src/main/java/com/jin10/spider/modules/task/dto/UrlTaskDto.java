package com.jin10.spider.modules.task.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jin10.spider.common.bean.TemplateDto;
import com.jin10.spider.modules.template.entity.Template;
import lombok.Data;

import java.util.Date;

/**
 * @author hongda.fang
 * @date 2019-11-12 16:08
 * ----------------------------------------------
 */
@Data
public class UrlTaskDto  {
    /**
     * 爬虫地址
     */
    private String url;


    /**
     * 任务唯一ID
     */
    private String taskUuid;


    /**
     * 是否国外网站
     */
    private boolean weForeign;


    /**
     * 创建时间
     */
    private Date createTime;


    private TemplateDto temp;

    @JsonIgnore
    private Template curTemp = new Template();

}
