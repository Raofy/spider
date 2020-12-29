package com.jin10.spider.spiderserver.utils;

import com.alibaba.fastjson.JSON;
import com.jin10.spider.common.bean.BaseResponse;
import com.jin10.spider.spiderserver.entity.PreUser;
import com.jin10.spider.common.exception.BaseException;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Airey
 * @date 2019/11/11 15:48
 * ----------------------------------------------
 * 安全服务工具类
 * ----------------------------------------------
 */
@UtilityClass
public class SecurityUtil {

    /**
     * 获取用户
     *
     * @param authentication
     * @return
     */
    private PreUser getUser(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof PreUser) {
            return (PreUser) principal;
        }
        return null;
    }

    public void writeJavaScript(BaseResponse base, HttpServletResponse response) throws IOException {

        response.setStatus(200);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter printWriter = response.getWriter();
        printWriter.write(JSON.toJSONString(base));
        printWriter.flush();
    }

    /**
     * 获取Authentication
     * @return
     */
    private Authentication getAuthentication(){
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 获取用户
     * @return
     */
    public PreUser getUser(){

        try {
            return (PreUser) getAuthentication().getPrincipal();
        } catch (Exception e) {
            throw new BaseException("登录状态过期", HttpStatus.UNAUTHORIZED.value());
        }

    }


}
