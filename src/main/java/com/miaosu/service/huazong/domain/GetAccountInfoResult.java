package com.miaosu.service.huazong.domain;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 余额查询
 */
public class GetAccountInfoResult extends Result {
    /**
     * 账户余额
     */
    @JSONField(name = "AccountBalance")
    private String AccountBalance;

    public String getAccountBalance() {
        return AccountBalance;
    }

    public void setAccountBalance(String accountBalance) {
        AccountBalance = accountBalance;
    }
}
