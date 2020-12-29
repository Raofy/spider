package com.jin10.spider.spiderserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * <p>
 * 筛选爬虫消息列表
 * </p>
 *
 * @author Airey
 * @since 2019-12-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SpiderMessageScreen implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @NotNull
    private Long userId;

    @NotNull
    private String msgId;

    /**
     * 分发任务id
     */
    private String taskId;

    /**
     * 模板id
     */
    private Integer tempId;

    /**
     * 部门id
     */
    private Integer deptId;

    /**
     * 爬虫内容标题
     */
    private String title;

    /**
     * 爬虫内容对应的url
     */
    private String url;

    /**
     * 网站来源
     */
    private String source;

    /**
     * 消息类型  1.文章 2.快讯 3.楼市 等
     */
    private String category;

    /**
     * 爬虫爬取的日期
     */
    private Date time;

    /**
     * 备注
     */
    private String remark;

    /**
     * 同一个标题可能有多个不同的频道
     */
    private String channel;


    /**
     * 该消息是否已经被推送
     */
    private boolean pushFlag;

    /**
     * 入库时间
     */
    private Date insertTime;


}
