package com.jin10.spider.modules.task.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author hongda.fang
 * @date 2019-12-05 14:35
 * ----------------------------------------------
 */
@Data
public class VerifyIpRequest {

    @NotNull
    private String ip;


    private String extra;


    private boolean wheForeign;

    /**
     * 权限控制类
     */
    @Data
    public static class AuthRequest {

        /**
         * 用户名
         */
        String name;

        /**
         * 密码
         */
        String pwd;

    }


}
