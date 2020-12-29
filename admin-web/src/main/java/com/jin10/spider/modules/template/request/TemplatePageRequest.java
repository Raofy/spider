package com.jin10.spider.modules.template.request;

import com.jin10.spider.common.bean.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author hongda.fang
 * @date 2019-12-09 17:07
 * ----------------------------------------------
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TemplatePageRequest extends BasePageRequest {

    private Integer status;
    private String title;
    private String pageSite;
    private String channel;
    private String category;

    private Boolean weforeign;

    private Integer proxyLevel;

    private Integer tempId;

    /**
     * 维护人员账号
     */
    private String maintainer;

    private boolean showDetail;

    private Date expireTime;


}
