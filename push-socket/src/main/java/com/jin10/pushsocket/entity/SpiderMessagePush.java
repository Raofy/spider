package com.jin10.pushsocket.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 推送爬虫消息列表
 * </p>
 *
 * @author Airey
 * @since 2020-03-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SpiderMessagePush implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

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
     * 环境变量  prod 生产环境  dev 测试环境
     */
    private String env;

    /**
     * 入库时间
     */
    private Date insertTime;


}
