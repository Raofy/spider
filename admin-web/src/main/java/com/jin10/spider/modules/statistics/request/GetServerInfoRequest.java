package com.jin10.spider.modules.statistics.request;

import com.jin10.spider.common.request.BaseSocketRequest;
import lombok.Data;

/**
 * @author hongda.fang
 * @date 2019-12-11 10:51
 * ----------------------------------------------
 */
@Data
public class GetServerInfoRequest extends BaseSocketRequest {
    /**
     * 是否第一次链接
     */
    private boolean isFirstConn;


}
