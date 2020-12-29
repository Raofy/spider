package com.jin10.pushsocket.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 消息类别管理
 * </p>
 *
 * @author Airey
 * @since 2019-11-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SpiderCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息类别管理
     */
    @TableId(value = "cate_id", type = IdType.AUTO)
    private Long cateId;

    /**
     * 类别名称
     */

    private String categoryName;

    /**
     * 类别颜色
     */

    private String categoryColor;


}
