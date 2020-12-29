package com.jin10.pushsocket.bean;

import lombok.Data;


@Data
public class BaseResponse {
    private static final String DEFAULT_SUCCESS_MESSAGE = null;
    private static final String DEFAULT_ERROR_MESSAGE = "error";


    private int code;
    private String msg;
    private Object data;

    public BaseResponse() {

    }

    public BaseResponse(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public BaseResponse(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }



    public static BaseResponse error(int errorCode, String message) {
        return new BaseResponse(errorCode, message);
    }

    public static BaseResponse ok() {
        return new BaseResponse(200,"访问成功！");
    }






}
