package com.jin10.pushsocket.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Airey
 * @date 2019/11/8 18:01
 * ----------------------------------------------
 * TODO
 * ----------------------------------------------
 */
@Getter
@AllArgsConstructor
public enum ResultCodeEnum {


    SUCCESS(200, "访问成功！"),

    DUPLICATEKEY_EXCEPTION(300, "数据库中已存在该记录"),

    UNAUTHORIZED(401, "JWT认证失败"),

    ACCESSDENIED(403, "权限不足,无法访问系统资源"),

    NOT_FOUND(404, "路径不存在，请检查路径是否正确"),

    DEFAULT_ERROR(500, "访问错误")


    ;

    private int code;

    private String message;


}
