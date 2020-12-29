package com.jin10.spider.spiderserver.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Airey
 * @date 2019/11/18 17:50
 * ----------------------------------------------
 * 爬虫消息状态
 * ----------------------------------------------
 */
@Getter
@AllArgsConstructor
public enum MessageStaus {


    NULL_CATEGORY(1, "category为空！"),

    ILLEGAL_CATEGROY(2, "非法的category类型");


    private int code;

    private String message;


}
