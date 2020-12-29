package com.jin10.spider.spiderserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 爬虫模版
 * </p>
 *
 * @author Airey
 * @since 2020-06-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Template implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 页面起始网址
     */
    private String pageSite;

    private String domainName;

    /**
     * 重试失败次数
     */
    private Integer retryTimes;

    /**
     * 抓取真正网址
     */
    private String realSite;

    /**
     * 页面标题，默认为网页标题
     */
    private String title;

    /**
     * 去重方式 ：1 通过链接，2通过文字
     */
    private Integer filterBy;

    /**
     * 当前页面监控状态 0停止  1运行中  2出错
     */
    private Integer status;

    /**
     * 页面爬取间隔，单位为毫秒，默认20秒钟爬取一次
     */
    private Integer intervalTime;

    /**
     * 同一个标题可能有多个不同的频道
     */
    private String channel;

    /**
     * 分类
     */
    private String category;

    private String encoding;

    /**
     * 添加监控页面时间
     */
    private Date createTime;

    /**
     * 页面优先级
     */
    private Integer priority;

    private Boolean weforeign;

    /**
     * 所属部门
     */
    private Long deptId;

    private Date updateTime;

    private Boolean special;

    /**
     * 拼接URL随机参数
     */
    private Boolean randomQuery;

    /**
     * 超时时间  默认7s
     */
    private Integer timeout;

    private String detailEncoding;

    /**
     * 备注
     */
    private String remark;

    private Integer deleted;

    /**
     * 解密类型
     */
    private String encryptType;

    /**
     * 特殊爬虫模版的id
     */
    private Long spiderTempId;

    private Integer proxyLevel;

    /**
     * 模版预计过期时间
     */
    private Date expireTime;

    /**
     * 操作用户
     */
    private String createUser;

    /**
     * 操作用户
     */
    private String updateUser;

    /**
     * 操作用户
     */
    private String deleteUser;

    /**
     * 删除时间
     */
    private Date deleteTime;

    /**
     * 停止原因
     */
    private String stopReason;

    /**
     * 停止时间
     */
    private Date stopTime;


}
