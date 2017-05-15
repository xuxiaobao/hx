package com.miaosu.service.recharge;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 充值结果对象
 *
 * @author CaoQi
 * @Time 2015/12/27
 */
public class RechargeResult {

    /**
     * 秒速订单号
     */
    private String orderId;

    /**
     * 充值订单号
     */
    private String rechargeId;

    /**
     * 返回码
     */
    private String code;

    /**
     * 返回秒速
     */
    private String msg;

    public String getRechargeId() {
        return rechargeId;
    }

    public void setRechargeId(String rechargeId) {
        this.rechargeId = rechargeId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("orderId", orderId)
                .append("rechargeId", rechargeId)
                .append("code", code)
                .append("msg", msg)
                .toString();
    }
}
