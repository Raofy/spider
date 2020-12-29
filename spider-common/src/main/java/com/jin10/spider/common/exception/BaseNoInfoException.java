package com.jin10.spider.common.exception;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author zxlei
 * @date   2019/11/11  11:47
 * ----------------------------------------------
 * 自定义异常
 * ----------------------------------------------
 */
public class BaseNoInfoException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = 1L;

    @Setter
    @Getter
    private String msg;

    @Setter
    @Getter
    private int code = 500;

    public BaseNoInfoException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public BaseNoInfoException(String msg, Throwable e) {
        super(msg, e);
        this.msg = msg;
    }

    public BaseNoInfoException(String msg, int code) {
        super(msg);
        this.msg = msg;
        this.code = code;
    }

    public BaseNoInfoException(String msg, int code, Throwable e) {
        super(msg, e);
        this.msg = msg;
        this.code = code;
    }

}
