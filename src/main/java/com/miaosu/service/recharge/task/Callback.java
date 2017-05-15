package com.miaosu.service.recharge.task;

import com.miaosu.model.enums.RechargeState;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * 回调对象
 */
public class Callback implements Serializable{

    private String orderId;

    private String transId;

    private String userName;

    private RechargeState rechargeState;

    private String notifyUrl;

    private String failedReason;

    public Callback(String orderId, String transId, String userName, RechargeState rechargeState, String notifyUrl, String failedReason) {
        this.orderId = orderId;
        this.transId = transId;
        this.userName = userName;
        this.rechargeState = rechargeState;
        this.notifyUrl = notifyUrl;
        this.failedReason = failedReason;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public RechargeState getRechargeState() {
        return rechargeState;
    }

    public void setRechargeState(RechargeState rechargeState) {
        this.rechargeState = rechargeState;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getFailedReason() {
        return failedReason;
    }

    public void setFailedReason(String failedReason) {
        this.failedReason = failedReason;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("orderId", orderId)
                .append("transId", transId)
                .append("userName", userName)
                .append("rechargeState", rechargeState)
                .append("notifyUrl", notifyUrl)
                .append("failedReason", failedReason)
                .toString();
    }
}
