package com.miaosu.service.huazong.domain;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Data;
import lombok.ToString;

/**
 * 订购接口返回对象
 * Created by angus on 15/10/6.
 */
@Data
@ToString(callSuper=true)
public class OrderFlowResult extends Result {
    /**
     * 状态（Y:订购已受理,N:失败)
     */
    @JSONField(name = "Status")
    private String status;

    /**
     * 失败原因(成功时为空值)
     */
    @JSONField(name = "FailedReason")
    private String failedReason;

    /**
     * 订单编号
     */
    @JSONField(name ="OrderNo")
    private String orderNo;
}
