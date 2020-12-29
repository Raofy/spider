package com.jin10.spider.common.request;

import com.jin10.spider.common.constants.ActionCodeConstants;
import lombok.Data;

/**
 * @author hongda.fang
 * @date 2019-11-22 17:43
 * ----------------------------------------------
 * 上下线监控请求
 */
@Data
public class SocketActiveRequest {

    private int action = ActionCodeConstants.SOCKET_ACTIVE;
    private String ip;
    private int status;

}
