package com.miaosu.base;

import java.io.Serializable;

/**
 * 结果信息实体Bean
 */
public class ResultInfo<T> implements Serializable{


    private boolean success;

    /**
     * <pre>
     * 结果码，六位数字设计: ABCDEF；
     * AB代表系统，00：通用系统；
     * CD代表模块，00：通用模块；
     * EF代表结果，00：成功，01：失败；
     *
     * 示例：{@link ResultCode}
     * 000000 -> 表示所有系统所有模块操作成功；
     * 000001 -> 表示所有系统所有模块操作失败；
     * </pre>
     */
    private String code;

    private String message;

    private T data;

    public ResultInfo( boolean isSuccess, ResultCode resultCode){
        this.success = isSuccess;
        this.code = resultCode.getCode();
        this.message = resultCode.getMsg();
    }

    public ResultInfo( boolean isSuccess, ResultCode resultCode,T data){
        this.success = isSuccess;
        this.message = resultCode.getMsg();
        this.code = resultCode.getCode();
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
