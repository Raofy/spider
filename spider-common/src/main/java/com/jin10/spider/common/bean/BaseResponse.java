package com.jin10.spider.common.bean;

import com.jin10.spider.common.enums.ResultCodeEnum;
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

    public BaseResponse(ResultCodeEnum resultCodeEnum) {
        this(resultCodeEnum.getCode(), resultCodeEnum.getMessage());
    }

    public BaseResponse(ResultCodeEnum resultCodeEnum, Object data) {
        this(resultCodeEnum.getCode(), resultCodeEnum.getMessage(), data);
    }

    public static BaseResponse ok() {
        return new BaseResponse(ResultCodeEnum.SUCCESS);
    }

    public static BaseResponse ok(Object data) {
        return new BaseResponse(ResultCodeEnum.SUCCESS, data);
    }


    public static BaseResponse error() {

        return new BaseResponse(ResultCodeEnum.DEFAULT_ERROR);
    }


    public static BaseResponse error(String message) {
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.code = ResultCodeEnum.DEFAULT_ERROR.getCode();
        baseResponse.msg = message;
        return baseResponse;
    }

    public static BaseResponse error(ResultCodeEnum resultCodeEnum) {
        return new BaseResponse(resultCodeEnum.getCode(), resultCodeEnum.getMessage());
    }


    public static BaseResponse error(int errorCode, String message) {
        return new BaseResponse(errorCode, message);
    }


    public static BaseResponse error(Object data) {
        return new BaseResponse(ResultCodeEnum.DEFAULT_ERROR, data);
    }


}
