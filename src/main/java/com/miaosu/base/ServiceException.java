package com.miaosu.base;

import org.springframework.context.i18n.LocaleContextHolder;

/**
 * 业务异常类
 * Created by angus on 15/9/28.
 */
public class ServiceException extends RuntimeException {

    private ResultCode errorCode;

    private String message;

    public ServiceException(ResultCode errorCode) {
        super(errorCode.getMsg());
        this.errorCode = errorCode;
    }

    public ServiceException(ResultCode errorCode,String message) {
        super(errorCode.getMsg());
        this.errorCode = errorCode;

        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ResultCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ResultCode errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }
}
