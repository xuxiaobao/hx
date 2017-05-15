package com.miaosu.report.orderstat;

import com.miaosu.model.OrderStat;

/**
 * OrderStatForm
 * Created by angus on 15/10/27.
 */
public class OrderStatForm extends OrderStat {
    private String rechargeOkRate;


    public String getRechargeOkRate() {
        return rechargeOkRate;
    }

    public void setRechargeOkRate(String rechargeOkRate) {
        this.rechargeOkRate = rechargeOkRate;
    }
}
