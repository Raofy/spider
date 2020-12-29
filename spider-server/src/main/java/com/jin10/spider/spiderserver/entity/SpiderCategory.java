package com.jin10.spider.spiderserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.web.bind.annotation.PathVariable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

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
    @NotBlank(message = "categoryName不能为空！")
    private String categoryName;

    /**
     * 类别颜色
     */
    @NotBlank(message = "categoryColor不能为空！")
    private String categoryColor;


}
