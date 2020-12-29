package com.jin10.spider.common.exception;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author Airey
 * @date 2020/4/2 15:58
 * ----------------------------------------------
 * 无效IP异常
 * ----------------------------------------------
 */

public class InvalidIpException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = 2767179343699504416L;

    @Setter
    @Getter
    private String msg;

    @Setter
    @Getter
    private int code = 500;

    public InvalidIpException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public InvalidIpException(String msg, Throwable e) {
        super(msg, e);
        this.msg = msg;
    }

    public InvalidIpException(String msg, int code) {
        super(msg);
        this.msg = msg;
        this.code = code;
    }

    public InvalidIpException(String msg, int code, Throwable e) {
        super(msg, e);
        this.msg = msg;
        this.code = code;
    }

}
