package com.jin10.spider.spiderserver.entity;

import com.jin10.spider.common.bean.BasePageRequest;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author Airey
 * @date 2020/3/2 9:22
 * ----------------------------------------------
 * 爬虫类别或标签
 * ----------------------------------------------
 */
@Data
public class SpiderCategoryOrLabel extends BasePageRequest {

    private Long id;


    /**
     * 类型 0- label  1- category
     */
    @NotBlank(message = "type不能为空！")
    private Integer type;

    @NotBlank(message = "name不能为空！")
    private String name;

    @NotBlank(message = "color不能为空！")
    private String color;


}
