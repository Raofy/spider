package com.jin10.pushsocket.bean;

import com.jin10.pushsocket.enums.MsgCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Airey
 * @date 2019/11/22 15:01
 * ----------------------------------------------
 * 返回给客户端的响应消息体
 * ----------------------------------------------
 */
@Data
@AllArgsConstructor
public class MsgResponse {

    private int msgType;

    private String msgDesc;

    private Object result;

    public MsgResponse() {

    }

    public MsgResponse(MsgCodeEnum msgCodeEnum, Object data) {
        this(msgCodeEnum.getCode(), msgCodeEnum.getMessage(), data);
    }



}
