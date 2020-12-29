package com.jin10.spider.spiderserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

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
public class SpiderMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;

    /**
     * 唯一消息id
     */
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
     * 消息来源，或者网站名称
     */
    private String source;

    /**
     * 消息类型 1.文章 2.快讯
     */
    private String category;

    /**
     * 同一个标题可能有多个不同的频道
     */
    private String channel;

    /**
     * 备注
     */
    private String remark;

    /**
     * 爬虫爬取的日期
     */
    private Date time;

    /**
     * 入库时间
     */
    private Date insertTime;

    /**
     * 类别颜色
     */
    @TableField(exist = false)
    private String categoryColor;



}
