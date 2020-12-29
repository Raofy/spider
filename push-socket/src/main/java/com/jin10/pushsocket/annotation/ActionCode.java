package com.jin10.pushsocket.annotation;

import java.lang.annotation.*;

/**
 * @author hongda.fang
 * @date 2019-11-08 16:02
 * ----------------------------------------------
 *
 * socket 动作码 注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface ActionCode {

    int value();
}
