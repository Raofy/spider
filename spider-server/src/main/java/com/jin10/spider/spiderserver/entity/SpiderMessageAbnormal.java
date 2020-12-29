package com.jin10.spider.spiderserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

import com.jin10.spider.spiderserver.enums.MessageStaus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 异常消息记录
 * </p>
 *
 * @author Airey
 * @since 2019-12-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SpiderMessageAbnormal implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 消息异常类型  1.category为空  2.category非法类型
     */
    private Integer exType;

    /**
     * 消息异常描述
     */
    private String exRemark;

    /**
     * 不正常消息
     */
    private String spiderMessage;

    /**
     * 入库时间
     */
    private Date insertTime;

    public SpiderMessageAbnormal(Integer exType, String exRemark, String spiderMessage) {
        this.exType = exType;
        this.exRemark = exRemark;
        this.spiderMessage = spiderMessage;
    }

    public SpiderMessageAbnormal(MessageStaus messageStaus, String spiderMessage) {
        this(messageStaus.getCode(), messageStaus.getMessage(), spiderMessage);
    }
}
