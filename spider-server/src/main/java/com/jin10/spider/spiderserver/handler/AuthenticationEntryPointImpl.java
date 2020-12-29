package com.jin10.spider.spiderserver.handler;

import com.jin10.spider.common.bean.BaseResponse;
import com.jin10.spider.common.enums.ResultCodeEnum;
import com.jin10.spider.spiderserver.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

/**
 * @author Airey
 * @date 2019/11/11 16:07
 * ----------------------------------------------
 *  认证失败处理类 返回401
 * ----------------------------------------------
 */
@Slf4j
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint, Serializable {

    private static final long serialVersionUID = -8970718410437077606L;

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        log.error("请求访问: " + httpServletRequest.getRequestURI() + " 接口， 经jwt认证失败，无法访问系统资源.");
        SecurityUtil.writeJavaScript(BaseResponse.error(ResultCodeEnum.UNAUTHORIZED),httpServletResponse);

    }
}
