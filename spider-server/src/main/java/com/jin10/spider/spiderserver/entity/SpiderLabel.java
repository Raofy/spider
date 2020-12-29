package com.jin10.spider.spiderserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 爬虫消息标签库
 * </p>
 *
 * @author Airey
 * @since 2019-11-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SpiderLabel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 标签名称
     */
    @NotBlank(message = "labelName不能为空！")
    private String labelName;

    /**
     * 标签颜色配置
     */
    @NotBlank(message = "labelColor不能为空！")
    private String labelColor;


}
