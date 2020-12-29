package com.jin10.spider.spiderserver.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 爬虫消息列表
 * </p>
 *
 * @author Airey
 * @since 2019-11-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Document(indexName = "spider_message",type = "docs",shards = 1,replicas = 0)
public class SpiderMessageElastics implements Serializable {

    private static final long serialVersionUID = 2824075622507638517L;


    /**
     * 唯一消息id
     */
    @Id
    private String msgId;

    /**
     * 分发任务id
     */
    @Field(type = FieldType.Keyword)
    private String taskId;

    /**
     * 模板id
     */
    @Field(type = FieldType.Integer)
    private Integer tempId;

    /**
     * 部门id
     */
    @Field(type = FieldType.Integer)
    private Integer deptId;

    /**
     * 爬虫内容标题
     */
    @Field(type = FieldType.Text,analyzer = "ik_max_word")
    private String title;

    /**
     * 爬虫内容对应的url
     */
    @Field(type = FieldType.Keyword)
    private String url;

    /**
     * 消息来源，或者网站名称
     */
    @Field(type = FieldType.Text)
    private String source;

    /**
     * 消息类型 1.文章 2.快讯
     */
    @Field(type = FieldType.Text)
    private String category;

    /**
     * 同一个标题可能有多个不同的频道
     */
    @Field(type = FieldType.Text)
    private String channel;

    /**
     * 备注
     */
    @Field(type = FieldType.Keyword)
    private String remark;

    /**
     * 爬虫爬取的日期
     */
    @Field(type = FieldType.Keyword)
    private Date time;

    /**
     * 入库时间
     */
    @Field(type = FieldType.Keyword)
    private Date insertTime;


    /**
     * 消息类型颜色
     */
    private String categoryColor;







}
