package com.jin10.spider.modules.task.request;

import com.jin10.spider.common.bean.BasePageRequest;
import lombok.Data;

/**
 * @author hongda.fang
 * @date 2019-12-17 11:19
 * ----------------------------------------------
 */
@Data
public class IpInfoPageRequest extends BasePageRequest {


    /**
     * 平台
     */
    private String platform;

    /**
     * 代理等级 高质量代理 低质量代理
     */
    private Integer proxyLevel;


    /**
     * 类型 http/ https/ socket
     */
    private String type;




}
