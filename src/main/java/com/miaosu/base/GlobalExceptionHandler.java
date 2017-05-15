package com.miaosu.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResultInfo defaultErrorHandler(HttpServletRequest request, Exception e)
            throws Exception {

        logger.error("全局异常处理 controller 调用发生错误",e);
        return new ResultInfo(false, ResultCode.FAILED);
    }

//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(value = ServiceException.class)
    @ResponseBody
    public ResultInfo serviceExceptionHandler(HttpServletRequest request, ServiceException e)
            throws Exception {

        logger.error("全局异常处理 controller 调用发生错误,{}",e.getErrorCode().getMsg());
        return new ResultInfo(false, e.getErrorCode());
    }
}
