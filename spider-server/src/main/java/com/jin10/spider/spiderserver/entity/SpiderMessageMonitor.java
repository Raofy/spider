package com.jin10.spider.spiderserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.util.Objects;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 消息来源监控
 * </p>
 *
 * @author Airey
 * @since 2019-12-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SpiderMessageMonitor implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 消息来源
     */
    private String source;

    /**
     * 消息类型
     */
    private String category;

    /**
     * 消息频道
     */
    private String channel;

    /**
     * 是否调度 0.是  1.否
     */
    private Integer dispatch;

    /**
     * 是否自动推送 0.是 1.否
     */
    private Integer autoPush;

    /**
     * 对应的链接网址
     */
    private String site;

    /**
     * 最后更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private Integer tempId;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpiderMessageMonitor)) return false;
        SpiderMessageMonitor that = (SpiderMessageMonitor) o;
        return getSource().equals(that.getSource()) &&
                getCategory().equals(that.getCategory()) &&
                getChannel().equals(that.getChannel());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSource(), getCategory(), getChannel());
    }
}
