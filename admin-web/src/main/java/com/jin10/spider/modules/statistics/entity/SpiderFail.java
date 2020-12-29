package com.jin10.spider.modules.statistics.entity;

import java.util.Date;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 爬虫失败信息
 * </p>
 *
 * @author hongda.fang
 * @since 2019-11-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SpiderFail implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private Long id;

    /**
     * ip 地址
     */
    private String ip;

    /**
     * 模版 id
     */
    private Long tempId;

    /**
     * 错误信息
     */
    private String errMsg;

    /**
     * 创建时间
     */
    private Date createTime;


    @TableField(exist = false)
    private int times;






}
