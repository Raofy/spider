package com.jin10.spider.spiderserver.handler;

import com.jin10.spider.common.bean.BaseResponse;
import com.jin10.spider.common.enums.ResultCodeEnum;
import com.jin10.spider.spiderserver.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

/**
 * @author Airey
 * @date 2019/11/11 15:45
 * ----------------------------------------------
 * TODO
 * ----------------------------------------------
 */
@Slf4j
@Component("AccessDeniedHandlerImpl")
public class AccessDeniedHandlerImpl implements AccessDeniedHandler, Serializable {

    private static final long serialVersionUID = -8970718410437077606L;

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        log.info("请求: " + httpServletRequest.getRequestURI() + " 权限不足，无法访问系统资源。");
        log.error(e.getMessage());
        SecurityUtil.writeJavaScript(BaseResponse.error(ResultCodeEnum.ACCESSDENIED), httpServletResponse);
    }
}
