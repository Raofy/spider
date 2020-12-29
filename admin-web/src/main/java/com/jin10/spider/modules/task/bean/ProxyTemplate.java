package com.jin10.spider.modules.task.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Airey
 * @date 2020/4/24 11:20
 * ----------------------------------------------
 * 代理模板
 * ----------------------------------------------
 */
@Data
@AllArgsConstructor
public class ProxyTemplate implements Serializable {


    private static final long serialVersionUID = 4582755782584004701L;


    public ProxyTemplate() {

    }


    /**
     * 代理等级
     */
    private Integer proxyLevel;

    /**
     * 是否国外网站 true 国外 false 国内
     */
    private Boolean wheForeign;


}
