package com.miaosu.service.recharge.domain;

import lombok.Data;

import java.util.List;

/**
 * Created by Administrator on 2017/5/20.
 */
@Data
public class AippNotifyResult {
    private String status;
    private String resultCode;
    private String resultDesc;
    private String orderId;
    private String channelOrderId;
    private String phoneNumber;
    private String prodId;
    private String prodName;
    private String channel;
    private String province;
    private String city;
    private String validBeginDate;
    private String validEndDate;
    private String validBeginTime;
    private String validEndTime;
    private String payAmount;
    private String ext;
    private String extDesc;

}
