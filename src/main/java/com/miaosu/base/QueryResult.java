package com.miaosu.base;

import java.io.Serializable;

/**
 * 查询结果集
 */
public class QueryResult<T> implements Serializable{

    public QueryResult(){}

    private boolean success;

    private Long totalCount;

    private T data;

    public QueryResult(boolean success, Long totalCount, T data) {
        this.success = success;
        this.totalCount = totalCount;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
