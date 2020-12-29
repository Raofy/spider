
package com.jin10.spider.modules.template.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 * 爬虫模版
 * </p>
 *
 * @author hongda.fang
 * @since 2019-10-29
 */
@Data
@Accessors(chain = true)
@TableName("template")
public class Template implements Serializable {

    private static final long serialVersionUID = -2814204783677035447L;

    @TableId
    private Long id;
    /**
     * 页面起始网址
     */
    @NotNull
    private String pageSite;
    /**
     * 顶级域名
     */
    private String domainName;
    /**
     * 抓取真正网址
     */
    private String realSite;
    /**
     * 页面标题，默认为网页标题
     */
    @NotNull
    private String title;
    /**
     * 当前页面监控状态 0停止  1运行中  2过期
     */
    private Integer status;
    /**
     * 重试次数(重试多少次失败停止模板)
     */
    private Integer retryTimes;
    /**
     * 页面爬取间隔，单位为毫秒，默认10秒钟爬取一次
     */
    private Integer intervalTime;
    /**
     * 同一个标题可能有多个不同的频道
     */
    @NotNull
    private String channel;
    /**
     * 分类
     */
    @NotNull
    private String category;

    private String detailEncoding;

    private String encoding;
    /**
     * 去重方式 ：0 不过滤 ｜1 根据url进行过滤 ｜ 2 根据网页源码进行过滤 ｜ 3 根据结果过滤
     */
    @Min(value = 0, message = "必须为0、1、2、3")
    @Max(value = 3, message = "必须为0、1、2、3")
    private Integer filterBy;
    /**
     * 页面优先级
     */
    @Max(value = 100, message = "不能超过100")
    private Integer priority;
    /**
     * 是否国外网址
     */
    private Boolean weforeign;
    /**
     * 所属部门
     */
    private Long deptId;
    /**
     * 是否特殊网站
     */
    private Boolean special;

    /**
     * 拼接URL随机参数
     */
    @TableField(value = "random_query")
    private Boolean randomQueryParams;

    /**
     * 超时时间
     */
    private Integer timeout;

    /**
     * 过期时间
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Date expireTime;
    /**
     * 拓展字段
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String remark;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long spiderTempId;

    private String encryptType;


    /**
     * 默认为0， 0 不需要代理 ｜ 1 随机代理 ｜ 2 底质量代理 ｜ 3 高质量代理
     */
    private Integer proxyLevel;
    /**
     * 添加监控页面时间
     */
    private Date createTime;
    private Date updateTime = new Date();
    private Date deleteTime;

    private String createUser;
    private String updateUser;
    private String deleteUser;

    /**
     * 停止时间
     */
    private Date stopTime;

    /**
     * 停止原因 1.手动停止 2.模板错误达到5次自动停止 3.模板过期
     */
    private String stopReason;


    @TableLogic
    private Integer deleted;


    @TableField(exist = false)
    private long preTime = 0;


    @TableField(exist = false)
    private int part = 0;


    @TableField(exist = false)
    private long pushNum = 0;


    /**
     * 并发数量
     */
    @TableField(exist = false)
    private AtomicInteger concurrentNum = new AtomicInteger(0);

    /**
     * 并发任务map
     */
    @TableField(exist = false)
    private ConcurrentHashMap<String, Long> taskUuidMap = new ConcurrentHashMap<>();

    /**
     * 推送标志
     */
    @TableField(exist = false)
    private boolean pushFlag = true;


    /**
     * 失败创建时间
     */
    @TableField(exist = false)
    private Date failTime;

    /**
     * 允许失败次数
     */
    @TableField(exist = false)
    private AtomicInteger allowFailTimes = new AtomicInteger(15);

    @TableField(exist = false)
    @NotNull
    private TemplateRule tempRule;

    @TableField(exist = false)
    private TemplateRule detailTempRule;


    public boolean WheProxy() {
        return proxyLevel > 0;
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return id.hashCode();
        }
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof Template) {
            Template template = (Template) obj;
            if (getId() != null && template.getId() != null) {
                return getId().equals(template.getId());
            }
        }
        return super.equals(obj);
    }

    public static void main(String[] args) {
        Template template = new Template();

        template.getConcurrentNum().incrementAndGet();
        template.getTaskUuidMap().put("sdfsdfs", System.currentTimeMillis());

        System.out.println(template);

    }
}
