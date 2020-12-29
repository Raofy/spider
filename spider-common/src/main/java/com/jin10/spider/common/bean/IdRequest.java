package com.jin10.spider.common.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

@ApiModel
public class IdRequest {

    @ApiModelProperty(value = "id")
    @NotNull(message =  "id 不能为空")
    private Long id;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
