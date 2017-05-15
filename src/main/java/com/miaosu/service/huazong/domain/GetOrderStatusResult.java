package com.miaosu.service.huazong.domain;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Data;
import lombok.ToString;

/**
 * 订购状态查询
 */
@Data
@ToString(callSuper=true)
public class GetOrderStatusResult extends Result {
    /**
     * 平台订单号
     */
    @JSONField(name ="OrderNo")
    private String orderNo;

    /**
     * 订购状态（Y:订购成功,N:订购失败,P:订购中)
     */
    @JSONField(name = "Status")
    private String status;

    /**
     * 失败原因
     */
    @JSONField(name ="FailedReason")
    private String failedReason;
    
    public static void main(String[] args) {
    	GetOrderStatusResult result = new GetOrderStatusResult();
    	result.setCode("111");
    	result.setFailedReason("failed");
    	result.setOrderNo("H111111");
    	result.setStatus("0");
    	System.out.println(result);
	}
}


