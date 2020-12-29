package com.jin10.spider.modules.template.request;

import lombok.Data;

/**
 * @author Airey
 * @date 2020/2/11 22:36
 * ----------------------------------------------
 * 模板续期请求实体
 * ----------------------------------------------
 */
@Data
public class RenewalRequest {

    /**
     * 模板id
     */
    private Long tempId;


    /**
     * 新的过期时间
     */
    private Long expireTime;


}
