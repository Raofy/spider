/**
 *
 *
 * https://www.jin10.com
 *
 *
 */

package com.jin10.spider.common.aspect;

import com.jin10.spider.common.exception.BaseException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Redis切面处理类
 *
 *
 */
@Aspect
@Component
public class RedisAspect {
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 是否开启redis缓存  true开启   false关闭
     */
    @Value("${jinshi.redis.open: false}")
    private boolean open;

    @Around("execution(* com.jin10.spider.common.utils.RedisUtilsBAK.*(..))")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Object result = null;
        if(open){
            try{
                result = point.proceed();
            }catch (Exception e){
                logger.error("redis error", e);
                throw new BaseException("Redis服务异常");
            }
        }
        return result;
    }
}
