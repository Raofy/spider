package com.jin10.pushsocket.annotation;

import java.lang.annotation.*;

/**
 * @author hongda.fang
 * @date 2019-11-09 16:02
 * ----------------------------------------------
 *
 * socket 返回 数据 注解
 * 自动解析为 json 并返回
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface SocketResponseBody {

}
