package com.jin10.spider.modules.task.entity;

import java.util.Date;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 代理ip 信息
 * </p>
 *
 * @author Airey
 * @since 2020-03-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class InvadedIpInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private Long id;
    /**
     * ip:端口、113.120.61.166:22989
     */
    private String ip;

    private Boolean wheForeign;

    /**
     * 地区
     */
    private String area;

    /**
     * 代理平台
     */
    private String platform;

    /**
     * ip 延迟 ms
     */
    private Integer delay;

    /**
     * 检查次数
     */
    private Integer checkTimes;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 检验时间
     */
    private Date checkTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 生存时间
     */
    private Long lifeTime;

    private Integer proxyLevel;

    private Integer deleted;

    /**
     * 是否有效 0-无效 1-有效
     */
    private Integer wheValid;

    /**
     * 类型 http/ https/ socket
     */
    private String type;

    private String extra;


}
