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
 * 分组
 * </p>
 *
 * @author Airey
 * @since 2020-08-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 分组名称
     */
    private String name;

    /**
     * 插入时间
     */
    private Date insertTime;

    private Date updateTime;


}
