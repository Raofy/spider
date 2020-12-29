package com.jin10.pushsocket.aspect;

import cn.hutool.core.util.ObjectUtil;
import com.jin10.pushsocket.utils.JsonUtils2;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @author hongda.fang
 * @date 2019-11-11 12:01
 * ----------------------------------------------
 */
@Aspect
@Component
public class SocketResponseBodyAspect {

    @Pointcut("@annotation(com.jin10.pushsocket.annotation.SocketResponseBody)")
    public void pointCut() {

    }

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {

        /**
         * 获取返回结果
         */
        Object proceed = point.proceed();

        if (ObjectUtil.isNull(proceed)) {
            return null;
        }

        /**
         * 获取方法参数
         */
        Object[] args = point.getArgs();

        if (args.length > 0) {
            if (args[0] instanceof ChannelHandlerContext) {
                TextWebSocketFrame tws = new TextWebSocketFrame(JsonUtils2.writeValue(proceed));
                ChannelHandlerContext context = (ChannelHandlerContext) args[0];
                context.writeAndFlush(tws);
            }
        }
        return proceed;
    }

}
