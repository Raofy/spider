package com.jin10.spider.spiderserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 分组-源  黑名单
 * </p>
 *
 * @author Airey
 * @since 2020-08-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysGroupMonitor implements Serializable {

    private static final long serialVersionUID = 1L;


    private Integer groupId;

    /**
     * 来源
     */
    private String source;

    /**
     * 分类
     */
    private String category;

    /**
     * 消息频道
     */
    private String channel;

    /**
     * 设置时间
     */
    private Date insertTime;

    private String updator;


}
