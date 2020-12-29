package com.jin10.spider.spiderserver.form;


import com.jin10.spider.common.bean.BasePageRequest;
import lombok.Data;

/**
 * @author Airey
 * @date 2019/12/17 17:11
 * ----------------------------------------------
 * TODO
 * ----------------------------------------------
 */
@Data
public class SpiderMessageMonitorForm extends BasePageRequest {


    private Long id;

    /**
     * 分类
     */
    private String category;

    /**
     * 关键字
     */
    private String keyword;

    /**
     * 是否自动推送 0 是 1 否
     */
    private Integer autoPush;

    /**
     * 是否调度   0 是 1 否
     */
    private Integer dispatch;




}
