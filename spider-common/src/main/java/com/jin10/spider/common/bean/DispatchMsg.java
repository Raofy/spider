package com.jin10.spider.common.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Airey
 * @date 2019/12/9 10:32
 * ----------------------------------------------
 * 监控源信息(通知钉钉)
 * ----------------------------------------------
 */
@Data
public class DispatchMsg implements Serializable {


    private static final long serialVersionUID = 2612514184472655605L;

    private String title;

    private String text;

    private String monitorUrl;

    private String targetUrl;


}
