package com.jin10.spider.common.trans;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: pancg
 * @Date: 2020/7/14 15:16
 */
@Data
public class TransResponse implements Serializable {
    /**
     * 源语言
     */
    private String from;

    /**
     * 目标语言
     */
    private String to;

    /**
     * 翻译结果
     */
    @JsonProperty("trans_result")
    private List<TransResult> transResult;

    /**
     * 错误码 仅当出现时错误时显示
     */
    @JsonProperty("error_code")
    private Integer errorCode;
}
