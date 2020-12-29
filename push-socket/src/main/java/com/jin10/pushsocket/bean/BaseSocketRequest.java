package com.jin10.pushsocket.bean;

import cn.hutool.json.JSONUtil;
import lombok.Data;

/**
 * @author hongda.fang
 * @date 2019-11-06 10:12
 * ----------------------------------------------
 * socket 请求体基础类
 */
@Data
public class BaseSocketRequest {

    private Integer action;
    private String ip;


    /**
     * 获取对应请求的bean
     * @param json
     * @return 对应的bean
     */
    public static BaseSocketRequest toBean(String json){
        if(JSONUtil.isJson(json)){
            BaseSocketRequest socketRequest = JSONUtil.toBean(json, BaseSocketRequest.class);
            if (socketRequest != null && socketRequest.getAction() != null){
                return socketRequest;
            }
        }
        return null;
    }
}
