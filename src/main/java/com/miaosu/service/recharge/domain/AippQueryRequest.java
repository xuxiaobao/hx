package com.miaosu.service.recharge.domain;

import lombok.Data;

/**
 * Created by Administrator on 2017/5/20.
 */
@Data
public class AippQueryRequest {
    private String orderId;
    private String orderTime;
    private String channelOrderId;

}
