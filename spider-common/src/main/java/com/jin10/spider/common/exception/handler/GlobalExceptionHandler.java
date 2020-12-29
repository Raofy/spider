package com.jin10.spider.common.exception.handler;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.jin10.spider.common.bean.BaseResponse;
import com.jin10.spider.common.enums.ResultCodeEnum;
import com.jin10.spider.common.exception.BaseException;
import com.jin10.spider.common.exception.BaseNoInfoException;
import com.jin10.spider.common.exception.InvalidIpException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.security.auth.login.AccountExpiredException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.nio.file.AccessDeniedException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

/**
 * @author Airey
 * @date 2019/12/10 15:18
 * ----------------------------------------------
 * 全局异常处理类
 * ----------------------------------------------
 */
@Slf4j
@RestController
@ControllerAdvice
@EnableConfigurationProperties({ServerProperties.class})
public class GlobalExceptionHandler implements ErrorController {


    private ErrorAttributes errorAttributes;

    @Autowired
    private ServerProperties serverProperties;


    /**
     * 处理自定义异常
     */
    @ExceptionHandler(BaseException.class)
    public BaseResponse handleRRException(BaseException e) {
        log.error(e.getMessage(), e);
        return BaseResponse.error(e.getCode(), e.getMsg());
    }

    /**
     * 处理ip校验异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(InvalidIpException.class)
    public BaseResponse handleInvaildIpException(InvalidIpException e) {
        log.error(e.getMessage());
        return BaseResponse.error(e.getCode(), e.getMessage());
    }


    @ExceptionHandler(BaseNoInfoException.class)
    public BaseResponse handleBaseNoInfoException(BaseNoInfoException e) {
        log.error(e.getMessage());
        return BaseResponse.error(e.getCode(), e.getMessage());
    }


    @ExceptionHandler(NoHandlerFoundException.class)
    public BaseResponse handlerNoFoundException(Exception e) {
        log.error(e.getMessage(), e);
        return BaseResponse.error(ResultCodeEnum.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public BaseResponse handleDuplicateKeyException(DuplicateKeyException e) {
        log.error(e.getMessage(), e);
        return BaseResponse.error(ResultCodeEnum.DUPLICATEKEY_EXCEPTION);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public BaseResponse handleAuthorizationException(AccessDeniedException e) {
        log.error(e.getMessage(), e);
        return BaseResponse.error(ResultCodeEnum.ACCESSDENIED);
    }

    @ExceptionHandler(AccountExpiredException.class)
    public BaseResponse handleAccountExpiredException(AccountExpiredException e) {
        log.error(e.getMessage(), e);
        return BaseResponse.error(e.getMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public BaseResponse handleUsernameNotFoundException(UsernameNotFoundException e) {
        log.error(e.getMessage(), e);
        return BaseResponse.error(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public BaseResponse handleException(Exception e) {
        log.error(e.getMessage(), e);
        return BaseResponse.error("服务器执行异常！！！");
    }


    @ExceptionHandler(SQLException.class)
    public BaseResponse handleSQLException(SQLException e) {
        log.error(e.getMessage(), e);
        return BaseResponse.error("sql异常！！！");
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        StringBuffer sb = new StringBuffer();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            sb.append(fieldError.getField());
            sb.append(":");
            sb.append(fieldError.getDefaultMessage());
        }
        log.error(e.getMessage(), e);
        return BaseResponse.error(sb.toString());
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public BaseResponse handleConstraintViolationException(ConstraintViolationException e) {
        StringBuffer sb = new StringBuffer();
        ConstraintViolationException exs = (ConstraintViolationException) e;
        Set<ConstraintViolation<?>> violations = exs.getConstraintViolations();
        for (ConstraintViolation<?> item : violations) {
            sb.append(item.getMessage() + "/");
        }
        log.error(e.getMessage(), e);
        return BaseResponse.error(sb.toString());
    }


    /**
     * 初始化GlobalExceptionHandler
     *
     * @param errorAttributes
     */
    @Autowired
    public GlobalExceptionHandler(ErrorAttributes errorAttributes) {
        Assert.notNull(errorAttributes, "ErrorAttributes must not be null");
        this.errorAttributes = errorAttributes;
    }

    @RequestMapping(value = "/error")
    @ResponseBody
    public BaseResponse error(HttpServletRequest request) {
        Map<String, Object> body = getErrorAttributes(request, isIncludeStackTrace(request, MediaType.ALL));
        HttpStatus status = getStatus(request);
        return BaseResponse.error(status.value(), body.get("message").toString());
    }


    /**
     * Determine if the stacktrace attribute should be included.
     *
     * @param request  the source request
     * @param produces the media type produced (or {@code MediaType.ALL})
     * @return if the stacktrace attribute should be included
     */
    protected boolean isIncludeStackTrace(HttpServletRequest request,
                                          MediaType produces) {
        ErrorProperties.IncludeStacktrace include = this.serverProperties.getError().getIncludeStacktrace();
        if (include == ErrorProperties.IncludeStacktrace.ALWAYS) {
            return true;
        }
        if (include == ErrorProperties.IncludeStacktrace.ON_TRACE_PARAM) {
            return getTraceParameter(request);
        }
        return false;
    }

    /**
     * 获取错误的信息
     *
     * @param request
     * @param includeStackTrace
     * @return
     */
    private Map<String, Object> getErrorAttributes(HttpServletRequest request, boolean includeStackTrace) {
        WebRequest webRequest = new ServletWebRequest(request);
        return this.errorAttributes.getErrorAttributes(webRequest, includeStackTrace);
    }

    /**
     * 是否包含trace
     *
     * @param request
     * @return
     */
    private boolean getTraceParameter(HttpServletRequest request) {
        String parameter = request.getParameter("trace");
        if (parameter == null) {
            return false;
        }
        return !"false".equals(parameter.toLowerCase());
    }

    /**
     * 获取错误编码
     *
     * @param request
     * @return
     */
    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request
                .getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        try {
            return HttpStatus.valueOf(statusCode);
        } catch (Exception ex) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }


    /**
     * 实现错误路径,暂时无用
     *
     * @return
     */
    @Override
    public String getErrorPath() {
        return null;
    }
}
