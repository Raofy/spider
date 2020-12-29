package com.jin10.spider.common.trans;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: pancg
 * @Date: 2020/7/14 15:17
 */
@Data
public class TransResult implements Serializable {

    /**
     * 原文
     */
    @JsonProperty(value = "src")
    private String  src;

    /**
     * 译文
     */
    @JsonProperty(value = "dst")
    private String  dst;
}
